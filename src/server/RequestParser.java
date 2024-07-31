package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for parsing HTTP requests. This class provides methods to parse
 * an HTTP request from a {@link BufferedReader} and extract relevant information
 * such as HTTP command, URI, headers, and request body.
 */
public class RequestParser {

    /**
     * Parses an HTTP request from a {@link BufferedReader}.
     *
     * <p>This method reads the request line, headers, and body from the provided
     * {@link BufferedReader}. It splits the URI into segments and parameters,
     * extracts headers, and accumulates the request body.</p>
     *
     * @param reader The {@link BufferedReader} to read the HTTP request from.
     * @return A {@link RequestInfo} object containing the parsed request information.
     * @throws IOException If an I/O error occurs while reading the request.
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        StringBuilder content = new StringBuilder();
        String line;
        String[] firstLine = reader.readLine().split(" ");
        String httpCommand = firstLine[0];
        String uri = firstLine[1];
        String[] uriSplit = uri.split("\\?");
        String[] uriSegments = uriSplit[0].substring(1).split("/");
        Map<String, String> parameters = extractParametersUri(uriSplit.length > 1 ? uriSplit[1] : "");
        Map<String, String> headers = new HashMap<>();

        while (!(line = reader.readLine()).isEmpty()) {
            String[] lineSplit = line.split(":");
            if (lineSplit.length == 2) {
                headers.put(lineSplit[0].trim(), lineSplit[1].trim());
            }
        }
        if (headers.containsKey("Content-Length")) {
            while (!(line = reader.readLine()).isEmpty()) {
                if (line.contains("filename=")) {
                    String[] lineSplit = line.split("filename=");
                    parameters.put("filename", lineSplit[1]);
                }
            }
            while (!(line = reader.readLine()).isEmpty()) {
                content.append(line).append('\n');
            }
        }
        return new RequestInfo(httpCommand, uri, uriSegments, parameters, content.toString().getBytes());
    }

    /**
     * Extracts parameters from a URI query string.
     *
     * @param uriSplit The URI query string to extract parameters from.
     * @return A {@link Map} containing the extracted parameters as key-value pairs.
     */
    private static Map<String, String> extractParametersUri(String uriSplit) {
        Map<String, String> parameters = new HashMap<>();
        if (uriSplit.isEmpty()) {
            return parameters;
        }
        String[] lineSplit;
        for (String parameter : uriSplit.split("&")) {
            lineSplit = parameter.split("=");
            if (lineSplit.length == 2) {
                parameters.put(lineSplit[0], lineSplit[1]);
            }
        }
        return parameters;
    }

    /**
     * Represents the information extracted from an HTTP request.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        /**
         * Constructs a new {@code RequestInfo} object.
         *
         * @param httpCommand  The HTTP command (e.g., GET, POST).
         * @param uri          The URI of the request.
         * @param uriSegments  The segments of the URI split by '/'.
         * @param parameters   A {@link Map} of request parameters.
         * @param content      The content of the request body as a byte array.
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        /**
         * Returns the HTTP command (e.g., GET, POST).
         *
         * @return The HTTP command.
         */
        public String getHttpCommand() {
            return httpCommand;
        }

        /**
         * Returns the URI of the request.
         *
         * @return The URI.
         */
        public String getUri() {
            return uri;
        }

        /**
         * Returns the segments of the URI split by '/'.
         *
         * @return An array of URI segments.
         */
        public String[] getUriSegments() {
            return uriSegments;
        }

        /**
         * Returns the request parameters.
         *
         * @return A {@link Map} of request parameters.
         */
        public Map<String, String> getParameters() {
            return parameters;
        }

        /**
         * Returns the content of the request body as a byte array.
         *
         * @return The request content.
         */
        public byte[] getContent() {
            return content;
        }
    }
}