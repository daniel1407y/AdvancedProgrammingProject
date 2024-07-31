package tests;

import server.HTTPServer;
import server.MyHTTPServer;
import servlets.ConfLoader;
import servlets.HtmlLoader;
import servlets.TopicDisplayer;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test6 {

    public static void main(String[] args) throws Exception {
        HTTPServer server=new MyHTTPServer(8080,5);
        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader());
        server.addServlet("GET", "/app/", new HtmlLoader("C:\\Users\\Eyal Arad\\IdeaProjects\\pp_ex2\\html_files"));
        server.start();
        testHtmlLoader();
        testConfLoader();
        System.in.read();
        server.close();
        System.out.println("done");
    }

    public static String testServlet(String httpRequest) throws Exception{
        Socket clientSocket = new Socket("localhost", 8080);

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(httpRequest);

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line = in.readLine();
        //while ((line = in.readLine())!= null)
        response.append(line);
        in.close();
        out.close();
        clientSocket.close();
        return response.toString();
    }


    public static void testHtmlLoader() throws Exception {
        String request = "GET /app/index.html HTTP/1.1\n" +
                "Host: example.com\n"+
                "\n" +
                "\n" +
                "\n" ;
        String response = testServlet(request);
        if (!response.equals("HTTP/1.1 200 OK"))
            System.out.println("server failed when shouldve work");

        request = "GET /app/dosentExist.wtf HTTP/1.1\n" +
                "Host: example.com\n"+
                "\n" +
                "\n" +
                "\n" ;
        response = testServlet(request);
        if (!response.equals("HTTP/1.1 404 Not Found"))
            System.out.println("server failed when shouldve work");
    }

    public static void testConfLoader() throws Exception {
        //test if work for general case
        String filePath = "C:\\Users\\Eyal Arad\\IdeaProjects\\pp_ex2\\config_files\\simple.conf";
        String content = readContent(filePath);
        String request = "POST /upload HTTP/1.1\n" +
                "Host: example.com\n" +
                "Content-Length: "+content.length()+"\n"+
                "\n" +
                "filename=\"simple.conf\"\n" +
                "\n"+
                content+
                "\n";
        String response = testServlet(request);
        if (!response.equals("HTTP/1.1 200 OK"))
            System.out.println("server failed when shouldve work");

        //test if return bad request when file has cycles
        filePath = "C:\\Users\\Eyal Arad\\IdeaProjects\\pp_ex2\\config_files\\withCycles.conf";
        content = readContent(filePath);
        request = "POST /upload HTTP/1.1\n" +
                "Host: example.com\n" +
                "Content-Length: "+content.length()+"\n"+
                "\n" +
                "filename=\"withCycles.conf\"\n" +
                "\n"+
                content+
                "\n";
        response = testServlet(request);
        if (response.equals("HTTP/1.1 200 OK"))
            System.out.println("server worked when should fail");
    }
    private static String readContent(String path)
    {   StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
