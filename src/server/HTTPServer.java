package server;

import servlets.Servlet;

/**
 * The {@code HTTPServer} interface defines the basic operations for an HTTP server.
 * It extends {@link Runnable} to allow the server to be run in a separate thread.
 */
public interface HTTPServer extends Runnable {

    /**
     * Adds a servlet to handle a specific HTTP command and URI.
     *
     * @param httpCommand The HTTP command (e.g., "GET", "POST", "DELETE").
     * @param uri         The URI path that the servlet will handle.
     * @param s           The {@link Servlet} instance to handle requests for the specified URI.
     */
    void addServlet(String httpCommand, String uri, Servlet s);

    /**
     * Removes a servlet for a specific HTTP command and URI.
     *
     * @param httpCommand The HTTP command (e.g., "GET", "POST", "DELETE").
     * @param uri         The URI path for which the servlet will be removed.
     */
    void removeServlet(String httpCommand, String uri);

    /**
     * Starts the HTTP server. This method initializes the server and begins accepting
     * client connections. It is typically used to start the server in a separate thread.
     */
    void start();

    /**
     * Closes the HTTP server, stopping it from accepting new connections and shutting
     * down any resources used by the server. This method is called to stop the server
     * gracefully.
     */
    void close();
}