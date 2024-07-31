package server;

import servlets.Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A simple HTTP server implementation that extends {@link Thread} and implements {@link HTTPServer}.
 * This server listens for HTTP requests on a specified port and dispatches them to registered servlets
 * based on the HTTP command (GET, POST, DELETE).
 */
public class MyHTTPServer extends Thread implements HTTPServer {

    private final int port;
    private final int nThreads;
    private final int TIMEOUT = 20;
    private final ExecutorService executorService;
    private final Map<String, Servlet> getMap;
    private final Map<String, Servlet> deleteMap;
    private final Map<String, Servlet> postMap;
    private ServerSocket serverSocket;
    private volatile boolean closed;

    /**
     * Constructs a new {@code MyHTTPServer} instance.
     *
     * @param port The port number on which the server will listen for incoming connections.
     * @param nThreads The number of threads in the thread pool for handling client requests.
     */
    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.nThreads = nThreads;
        this.getMap = new ConcurrentHashMap<>();
        this.deleteMap = new ConcurrentHashMap<>();
        this.postMap = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(nThreads);
    }

    /**
     * Adds a servlet to handle a specific HTTP command and URI.
     *
     * @param httpCommand The HTTP command (GET, POST, DELETE).
     * @param uri The URI path that the servlet will handle.
     * @param s The {@link Servlet} instance to handle the request.
     */
    public void addServlet(String httpCommand, String uri, Servlet s) {
        this.getHttpMap(httpCommand).put(uri, s);
    }

    /**
     * Removes a servlet for a specific HTTP command and URI.
     *
     * @param httpCommand The HTTP command (GET, POST, DELETE).
     * @param uri The URI path for which the servlet will be removed.
     */
    public void removeServlet(String httpCommand, String uri) {
        this.getHttpMap(httpCommand).remove(uri);
    }

    /**
     * Prints the keys of the map for a specific HTTP command.
     *
     * @param httpCommand The HTTP command (GET, POST, DELETE).
     */
    public void printMapKeys(String httpCommand) {
        System.out.println(getHttpMap(httpCommand).toString());
    }

    /**
     * Gets the map of servlets for a specific HTTP command.
     *
     * @param httpCommand The HTTP command (GET, POST, DELETE).
     * @return A map of URIs to servlets for the specified HTTP command.
     */
    private Map<String, Servlet> getHttpMap(String httpCommand) {
        switch (httpCommand) {
            case "GET":
                return getMap;
            case "POST":
                return postMap;
            case "DELETE":
                return deleteMap;
            default:
                return null;
        }
    }

    /**
     * Finds the appropriate servlet for the given request.
     *
     * @param ri The {@link RequestParser.RequestInfo} object containing request details.
     * @return The {@link Servlet} instance that matches the request, or {@code null} if no match is found.
     */
    private Servlet getServlet(RequestParser.RequestInfo ri) {
        String httpCommand = ri.getHttpCommand();
        String uri = ri.getUri();
        Servlet servlet = null;

        Map<String, Servlet> someMap = getHttpMap(httpCommand);
        if (someMap == null) {
            return null;
        }

        for (int i = uri.length() - 1; i >= 0; i--) { // Going backwards to find the longest match
            String partialUri = uri.substring(0, i + 1);
            servlet = someMap.get(partialUri);
            if (servlet != null) {
                break;
            }
        }
        return servlet;
    }

    /**
     * Sends a 500 Internal Server Error response to the client.
     *
     * @param toClient The {@link OutputStream} to write the error response to.
     */
    private void sendErrorMessage(OutputStream toClient) {
        try {
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.1 500 Server Failed\r\n");
            response.append("Content-Type: text/plain\r\n");
            response.append("\r\n");
            response.append("Server does not support this type of HTTP request");
            toClient.write(response.toString().getBytes());
        } catch (IOException e) {
            System.out.println("Failed to write error message");
        }
    }

    /**
     * Handles client connections and processes HTTP requests.
     *
     * @param client The {@link Socket} representing the client connection.
     */
    private void clientHandler(Socket client) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream output = client.getOutputStream()) {
            RequestParser.RequestInfo info = RequestParser.parseRequest(input);
            Servlet servlet = this.getServlet(info);
            if (servlet != null) {
                servlet.handle(info, output);
            } else {
                sendErrorMessage(output);
            }
            input.close();
            output.close();
            client.close();
            System.out.println("Client closed: " + client.getInetAddress());
        } catch (IOException e) {
            System.out.println("Client handler failed");
            e.printStackTrace();
        }
    }

    /**
     * Runs the server, accepting client connections and handling requests.
     */
    @Override
    public void run() {
        try {
            closed = false;
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000);
            System.out.println("Server is running on port: " + port);

            while (!closed) {
                try {
                    Socket clientSocket = this.serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    executorService.submit(() -> clientHandler(clientSocket));
                } catch (SocketTimeoutException e) {
                    // Ignore timeout exceptions
                }
            }
        } catch (IOException e) {
            System.out.println("Server run failed");
            e.printStackTrace();
        } finally {
            shutDown();
        }
    }

    /**
     * Shuts down the server, closing the server socket and terminating the executor service.
     */
    private void shutDown() {
        System.out.println("Shutting down server");
        try {
            serverSocket.close();
            executorService.shutdown();
            // Wait a while for existing tasks to terminate
            if (!executorService.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executorService.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
                    System.out.println("Executor service did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.out.println("Server did not close properly");
            throw new RuntimeException(e);
        }

        // Close all servlets
        String[] tempArr = {"GET", "POST", "DELETE"};
        try {
            for (String httpCommand : tempArr) {
                Map<String, Servlet> tempMap = this.getHttpMap(httpCommand);
                for (String uri : tempMap.keySet()) {
                    tempMap.get(uri).close();
                    tempMap.remove(uri);
                }
            }
        } catch (IOException e) {
            System.out.println("Error closing servlets");
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the server, stopping it from accepting new connections.
     */
    public void close() {
        closed = true;
    }
}
