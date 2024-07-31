package servlets;

import java.io.IOException;
import java.io.OutputStream;
import server.RequestParser.RequestInfo;

/**
 * Represents a servlet interface for handling HTTP requests.
 * Implementations of this interface will process HTTP requests and generate responses
 * for clients, and will also handle any necessary resource cleanup.
 */
public interface Servlet {

    /**
     * Handles an HTTP request and generates a response to be sent to the client.
     *
     * @param ri       The {@link RequestInfo} object containing information about the request.
     * @param toClient The {@link OutputStream} to which the response will be written.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;

    /**
     * Closes the servlet, releasing any resources if necessary.
     * This method should be called when the servlet is no longer needed.
     *
     * @throws IOException If an I/O error occurs while closing.
     */
    void close() throws IOException;
}