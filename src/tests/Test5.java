package tests;

import server.MyHTTPServer;
import server.RequestParser;
import servlets.Servlet;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Test5 {

    public static void testParseRequest()
    {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                "Host: example.com\n" +
                "Content-Length: 5\n"+
                "\n" +
                "filename=\"hello_world.txt\"\n"+
                "\n" +
                "hello world!\n"+
                "\n" ;

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);

            // Test HTTP command
            if (!requestInfo.getHttpCommand().equals("GET")) {
                System.out.println("HTTP command test failed (-5)");
            }

            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            }
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename","\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }


        //test parser with differenet type of request
       request = "POST /publish?id=123 HTTP/1.1\n" +
                "Host: example.com\n" +
                "Content-Type: plain/text\n"+
                "Content-Length: 5\n"+
                "\n" +
                "\n" +
                "hello world!\n"+
                "this test works\n"+
                "\n" ;

        input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);

            // Test HTTP command
            if (!requestInfo.getHttpCommand().equals("POST")) {
                System.out.println("HTTP command test failed (-5)");
            }

            // Test URI
            if (!requestInfo.getUri().equals("/publish?id=123")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"publish"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            }
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            byte[] expectedContent = ("hello world!\n"+ "this test works\n").getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }
        System.out.println("done testing RequestParser");
    }

    class HelloServlet implements Servlet {
        @Override
        public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
            String responseBody = "Hello, world!";
            toClient.write(("HTTP/1.1 200 OK\n" +
                    "Content-Type: text/plain\n" +
                    "Content-Length: " + responseBody.length() + "\n" +
                    "\n" +
                    responseBody).getBytes());
            toClient.flush();
        }

        @Override
        public void close() throws IOException {
            // No resources to close
        }
    }

    class addServlet implements Servlet {
        @Override
        public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
//       get parameters
            Map<String, String> params = ri.getParameters();
            String a = params.get("a");
            String b = params.get("b");
            int sum = Integer.parseInt(a) + Integer.parseInt(b);
            String responseBody = "Sum: " + sum;
            toClient.write(("HTTP/1.1 200 OK\n" +
                    "Content-Type: text/plain\n" +
                    "Content-Length: " + responseBody.length() + "\n" +
                    "\n" +
                    responseBody).getBytes());
            toClient.flush();
        }

        @Override
        public void close() throws IOException {
            // No resources to close
        }
    }



    public static String testServlet(String httpRequest) throws Exception{
        Socket clientSocket = new Socket("localhost", 8080);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(httpRequest);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String response = "";
        String line;
        while ((line = in.readLine()) != null) {
            response += line + "\n";
        }
        in.close();
        out.close();
        clientSocket.close();
        return response;
    }


    public static void testServer() throws Exception{

        class responseServlet implements Servlet{
            @Override
            public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
                String text = "Basic Server Response Check";
                toClient.write(("HTTP/1.1 200 OK\n"+
                        "Content-Type: text/plain\n"+
                        "Content-Length: " + text.length() +"\n"+
                        "\n"+
                        text).getBytes());
            }

            @Override
            public void close() throws IOException {
            }
        }

        class sumServlet implements Servlet{
            @Override
            public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
                Map<String, String> parameters = ri.getParameters();
                String num1 = parameters.get("num1");
                String num2 = parameters.get("num2");
                int sum = Integer.parseInt(num1) + Integer.parseInt(num2);

                String responseBody = "SumServlet result: " + sum;
                toClient.write(("HTTP/1.1 200 OK\n"+
                        "Content-Type: text/plain\n"+
                        "Content-Length: " + responseBody.length()+ "\n"+
                        "\n"+
                        responseBody).getBytes());
            }

            @Override
            public void close() throws IOException {
            }
        }

        int startThreads = Thread.activeCount();
        // create server and add servlets
        MyHTTPServer server = new MyHTTPServer(8080, 5);
        server.addServlet("GET", "/response", new responseServlet());
        server.addServlet("GET", "/response/sum", new sumServlet());
        server.start();

        if (Thread.activeCount()!=startThreads+1)
            System.out.println("Error: server didnt start thread");

        // Test response servlet, user sends a GET method to /response/short, expected to be received by /response
        String responseCheck = "GET /response/short HTTP/1.1\n";
        String response = testServlet(responseCheck);

        // checking if expected responses have been received by the clients
        if (!response.equals("HTTP/1.1 200 OK\n"+
                "Content-Type: text/plain\n"+
                "Content-Length: 27" +"\n"+
                "\n"+
                "Basic Server Response Check"+"\n")) {
            System.out.println("***response servlet test failed***");
        }


        // Test sum response servlet, user sends a GET method to /response/sum, expected to be received by /response/sum
        String sumCheck = "GET /response/sum?num1=3&num2=5 HTTP/1.1\n";
        String sumResponse = testServlet(sumCheck);

        if (!sumResponse.equals("HTTP/1.1 200 OK\n"+
                "Content-Type: text/plain\n"+
                "Content-Length: 20" +"\n"+
                "\n"+
                "SumServlet result: 8"+"\n")) {
            System.out.println("***sum servlet test failed***");
        }


        // close server, check active thread number
        server.close();
        Thread.sleep(2000);
        if(Thread.activeCount() != startThreads){
            System.out.println("***Server did not close all threads***");
        }
    }


    public static void main(String[] args) {
        testParseRequest(); // 40 points
        try{
            testServer(); // 60
        }catch(Exception e){
            System.out.println("your server throwed an exception (-60)");
        }
        System.out.println("done");
    }
}
