package servlets;

import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import server.RequestParser;
import servlets.Servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * A servlet that handles displaying topics and their latest messages in an HTML table format.
 * Implements the {@link Servlet} interface to process HTTP requests and generate responses.
 */
public class TopicDisplayer implements Servlet {

    /**
     * Constructs a new {@code TopicDisplayer} instance.
     */
    public TopicDisplayer() {
        // Default constructor
    }

    /**
     * Handles an HTTP request by processing the request parameters and generating an HTML response
     * that includes a table of topics and their latest messages.
     *
     * @param ri       The request information encapsulated in a {@link RequestParser.RequestInfo} object.
     * @param toClient The {@link OutputStream} to write the response to the client.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        Map<String, String> parameters = ri.getParameters();
        if (parameters.size() >= 2) {
            String topicName = parameters.get("topicName");
            Message message = new Message(parameters.get("message"));

            if (TopicManagerSingleton.get().topicSearch(topicName)) {
                TopicManagerSingleton.get().getTopic(topicName).publish(message);
            }

            String templateHtml = templateHtml();
            String newHtml = createTable(templateHtml);
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Content-Type: text/html\r\n");
            response.append("Access-Control-Allow-Origin: *\r\n");
            response.append("Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n");
            response.append("Access-Control-Allow-Headers: Origin, Content-Type, Accept, Authorization\r\n");
            response.append("\r\n");
            response.append(newHtml);
            toClient.write(response.toString().getBytes());
        }
        else {
            String response = "HTTP/1.1 400 Bad Request\r\n" +
                    "Content-Type: text/html\r\n" +
                    "\r\n" +
                    "<html><body><h1>400 Bad Request</h1><p>Illegal Request</p></body></html>";
            toClient.write(response.getBytes());
        }
    }

    /**
     * Creates an HTML table containing the list of topics and their latest messages.
     *
     * @param templateHtml The HTML template that will be used as the base for the table.
     * @return The HTML content with the populated table.
     */
    private String createTable(String templateHtml) {
        StringBuilder newHtml = new StringBuilder(templateHtml);
        StringBuilder table = new StringBuilder();
        String target = "<tbody>";
        int index = newHtml.indexOf(target);

        for (Topic topic : TopicManagerSingleton.get().getTopics()) {
            table.append("<tr><td>").append(topic.name)
                    .append("</td><td>").append(topic.getLastMessage().asText)
                    .append("</td></tr>\n");
        }

        newHtml.insert(index + target.length(), table.toString());
        return newHtml.toString();
    }

    /**
     * Provides the HTML template for the table display.
     *
     * @return The HTML template as a string.
     */
    public String templateHtml() {
        return "<!DOCTYPE html>\n"
    + "<html lang=\"en\">\n"
    + "<head>\n"
    + "    <meta charset=\"UTF-8\">\n"
    + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
    + "    <title>Styled HTML Table</title>\n"
    + "    <style>\n"
    + "        body {\n"
    + "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n"
    + "            background-color: #b7ced1;\n"
    + "            margin: 0;\n"
    + "            padding: 20px;\n"
    + "        }\n"
    + "        table {\n"
    + "            width: 90%;\n"
    + "            margin: 30px auto;\n"
    + "            border-collapse: collapse;\n"
    + "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\n"
    + "            transition: box-shadow 0.3s ease-in-out;\n"
    + "        }\n"
    + "        table:hover {\n"
    + "            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);\n"
    + "        }\n"
    + "        th, td {\n"
    + "            padding: 20px;\n"
    + "            text-align: left;\n"
    + "            border: 1px solid #ccc;\n"
    + "        }\n"
    + "        th {\n"
    + "            background-color: #00796b;\n"
    + "            color: #fff;\n"
    + "            font-weight: 600;\n"
    + "        }\n"
    + "        tr:nth-child(even) {\n"
    + "            background-color: #f0f0f0;\n"
    + "        }\n"
    + "        tr:hover {\n"
    + "            background-color: #b2dfdb;\n"
    + "            transition: background-color 0.2s ease-in-out;\n"
    + "        }\n"
    + "    </style>\n"
    + "</head>\n"
    + "<body>\n"
    + "<table>\n"
    + "    <thead>\n"
    + "    <tr>\n"
    + "        <th>Topic</th>\n"
    + "        <th>Value</th>\n"
    + "    </tr>\n"
    + "    </thead>\n"
    + "    <tbody>\n"
    + "    </tbody>\n"
    + "</table>\n"
    + "</body>\n"
    + "</html>";
    }

    /**
     * Closes the servlet, releasing any resources if necessary.
     *
     * @throws IOException If an I/O error occurs while closing.
     */
    @Override
    public void close() throws IOException {
        // No resources to close in this implementation.
    }
}
