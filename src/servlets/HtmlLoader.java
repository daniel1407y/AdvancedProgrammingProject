package servlets;

import server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A servlet that handles the loading and serving of HTML files from a specified directory.
 * Implements the {@link Servlet} interface to process HTTP requests and provide HTML file content
 * or an error message if the file is not found.
 */
public class HtmlLoader implements Servlet {

    private final Path path;

    /**
     * Constructs a new {@code HtmlLoader} instance with the specified directory path.
     *
     * @param path The directory path where HTML files are stored.
     */
    public HtmlLoader(String path) {
        this.path = Path.of(path);
    }

    /**
     * Handles an HTTP request by loading and serving the requested HTML file from the directory.
     * If the requested file exists, its content is sent to the client with a 200 OK status.
     * If the file does not exist, a 404 Not Found response is sent.
     *
     * @param ri       The {@link RequestParser.RequestInfo} object containing information about the request.
     * @param toClient The {@link OutputStream} to which the response will be written.
     * @throws IOException If an I/O error occurs while reading the file or writing to the output stream.
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        String[] uriSegments = ri.getUriSegments();
        String fileName = uriSegments[uriSegments.length - 1];
        Path filePath = path.resolve(fileName);
        StringBuilder request = new StringBuilder();

        // Check if the file exists in the directory
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            byte[] fileContent = Files.readAllBytes(filePath);
            request.append("HTTP/1.1 200 OK\r\n");
            request.append("Content-Type: text/html\r\n");
            request.append("Content-Length: ").append(fileContent.length).append("\r\n");
            request.append("\r\n");
            request.append(new String(fileContent));
            request.append("\r\n");
        } else {
            String content = "File was not found\r\n";
            System.out.println("File not found");
            request.append("HTTP/1.1 404 Not Found\r\n");
            request.append("Content-Type: text/html\r\n");
            request.append("Content-Length: ").append(content.length()).append("\r\n");
            request.append("\r\n");
            request.append(content);
            request.append("\r\n");
        }
        toClient.write(request.toString().getBytes());
    }

    /**
     * Closes the servlet, releasing any resources if necessary.
     * This method does not need to perform any actions in this implementation.
     *
     * @throws IOException If an I/O error occurs while closing.
     */
    @Override
    public void close() throws IOException {
        // No resources to close in this implementation.
    }
}