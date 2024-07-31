package servlets;

import Config.GenericConfig;
import Config.Graph;
import graph.TopicManagerSingleton;
import server.RequestParser;
import views.HtmlGraphWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A servlet that handles the loading and processing of configuration files,
 * and generates an HTML representation of a graph based on the configuration.
 * Implements the {@link Servlet} interface to process HTTP requests and
 * provide responses to clients.
 */
public class ConfLoader implements Servlet {

    private final String path;
    private final GenericConfig genericConfig;

    /**
     * Constructs a new {@code ConfLoader} instance, initializing the configuration directory.
     * If the directory does not exist, it is created.
     */
    public ConfLoader() {
        String path1;
        String folderName = "..\\created_configs";
        File file = new File(folderName);
        // Debugging output for absolute path
        String temp = file.getAbsolutePath();
        path1 = temp.substring(0, temp.length() - folderName.length()) + temp.substring(temp.length() - folderName.length() + 3);
        this.path = path1 + "\\";
        genericConfig = new GenericConfig();
        if (!Files.exists(Path.of(this.path))) {
            try {
                Files.createDirectories(Path.of(this.path));
                System.out.println("Directory created: " + this.path);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to create directory: " + this.path);
            }
        } else {
            System.out.println("Directory exists");
        }
    }

    /**
     * Handles HTTP requests that involve processing configuration files.
     * This method processes the request to save the configuration file, generate
     * an HTML representation of the graph, and send the appropriate response to the client.
     *
     * @param ri       The {@link RequestParser.RequestInfo} object containing information about the request.
     * @param toClient The {@link OutputStream} to which the response will be written.
     * @throws IOException If an I/O error occurs while handling the file or writing to the output stream.
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        genericConfig.close();
        TopicManagerSingleton.get().clear();
        StringBuilder response = new StringBuilder();
        try {
            String fileName = ri.getParameters().get("filename");
            if (fileName == null)
                throw new IllegalArgumentException("Filename was not found");

            fileName = fileName.substring(1, fileName.length() - 1);
            Path filePath = Path.of(path + fileName);
            String content = new String(ri.getContent(), StandardCharsets.UTF_8);

            if (!fileName.endsWith("conf")) {
                System.out.println("Bad file type");
                throw new IllegalArgumentException("File should be of type .conf");
            }
            if (Files.exists(filePath))
                Files.delete(filePath);
            Files.createFile(filePath);
            Files.write(filePath, content.getBytes());
            String htmlString = handleConfig(fileName).toString();
            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Content-Type: text/html\r\n");
            response.append("Content-Length: ").append(htmlString.length()).append("\r\n");
            response.append("Access-Control-Allow-Origin: *\r\n");
            response.append("Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n");
            response.append("Access-Control-Allow-Headers: Origin, Content-Type, Accept, Authorization\r\n");
            response.append("\r\n");
            response.append(htmlString);
            toClient.write(response.toString().getBytes());
        } catch (IOException e) {
            response.append("HTTP/1.1 500 Server Error\r\n");
            response.append("Content-Type: text/plain\r\n");
            response.append("\r\n");
            response.append("Server failed to handle file");
            toClient.write(response.toString().getBytes());
        } catch (IllegalArgumentException e) {
            handleBadFile(toClient, e.getMessage());
        }
    }

    /**
     * Handles bad file requests by sending a 400 Bad Request response to the client.
     *
     * @param toClient The {@link OutputStream} to which the response will be written.
     * @param msg      The message to be included in the response body.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    private void handleBadFile(OutputStream toClient, String msg) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 400 Bad Request\r\n");
        response.append("Content-Type: text/html\r\n");
        response.append("\r\n");
        response.append(msg);
        toClient.write(response.toString().getBytes());
    }

    /**
     * Processes the configuration file and generates an HTML representation of the graph.
     *
     * @param fileName The name of the configuration file.
     * @return A {@link StringBuilder} containing the HTML representation of the graph.
     * @throws IllegalArgumentException If the graph contains cycles.
     */
    private StringBuilder handleConfig(String fileName) throws IllegalArgumentException, IOException {
        genericConfig.setConfFile(path + fileName);
        genericConfig.create();
        Graph graph = new Graph();
        graph.createFromTopics();
        List<String> graphList = HtmlGraphWriter.getGraphHtml(graph);
        if (graph.hasCycles()) {
            throw new IllegalArgumentException("Graph has cycles");
        }
        String htmlString;
        StringBuilder htmlBuilder = new StringBuilder();
        for (String line : graphList) {
            htmlBuilder.append(line).append('\n');
        }
        return htmlBuilder;
    }

    /**
     * Closes the servlet and releases any resources if necessary.
     *
     * @throws IOException If an I/O error occurs while closing.
     */
    @Override
    public void close() throws IOException {
        genericConfig.close();
    }
}
