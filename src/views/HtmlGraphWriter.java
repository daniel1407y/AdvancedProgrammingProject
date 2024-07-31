package views;

import Config.Graph;
import Config.Node;
import graph.TopicManagerSingleton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for generating HTML content for graph visualization.
 * It processes a {@link Graph} object, extracting node and edge information, and
 * inserts it into a pre-existing HTML file to create a graphical representation of the graph.
 */
public class HtmlGraphWriter {

    /**
     * Generates a list of strings representing the HTML content for the graph.
     * This method reads an existing HTML file, processes the given graph, and
     * inserts nodes and edges into the appropriate sections of the HTML.
     *
     * @param graph The {@link Graph} object that contains nodes and edges to be visualized.
     * @return A list of strings representing the updated HTML content with graph data.
     * @throws IllegalArgumentException If there is an error reading the HTML file.
     */
    public static List<String> getGraphHtml(Graph graph) throws IllegalArgumentException, IOException {
        List<String> graphNodes = new ArrayList<>();
        List<String> graphEdges = new ArrayList<>();
        Map<String, Integer> idCreator = new HashMap<>();
        String path = "html_files/graph.html";

        // Process nodes
        for (Node node : graph) {
            int id = 0;
            if (node.getName().startsWith("A")) {
                String actualNodeName = node.getName().substring(1);
                if (idCreator.containsKey(actualNodeName)) {
                    id = idCreator.get(actualNodeName) + 1;
                }
                idCreator.put(actualNodeName, id);
                node.setName(node.getName() + Integer.toString(id));
                //add agent as a circle
                graphNodes.add(String.format("{ name: '%s', itemStyle: { color: '#01aeff'}},", actualNodeName + Integer.toString(id)));
            } else { // topic
                String actualNodeName = node.getName().substring(1);
                //add agent as a squre
                graphNodes.add(String.format("{ name: '%s', itemStyle: { color: '#41f66d'}, symbol: 'rect'},", actualNodeName));
            }
        }

        // Process edges
        for (Node node : graph) {
            String sourceNode = node.getName().substring(1);
            for (Node connectedNode : node.getEdges()) {
                String destNode = connectedNode.getName().substring(1);
                //add edges
                graphEdges.add(String.format("{ source: '%s', target: '%s' },", sourceNode, destNode));
            }
        }

        // Update HTML content with nodes and edges
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (int i = 0; i < lines.size(); i++) {
                //add nodes in saved place
                if (lines.get(i).contains("Nodes inserted here by HtmlGraphWriter.java")) {
                    lines.addAll(i + 1, graphNodes);
                }
                //add edges in saved place
                if (lines.get(i).contains("Edges inserted here by HtmlGraphWriter.java")) {
                    lines.addAll(i + 1, graphEdges);
                }
            }
            return lines;
        } catch (IOException e) {
            System.out.println("file didn't open");//in server failed to read template html file
            throw new IOException("Error reading HTML file", e);
        }
    }
}