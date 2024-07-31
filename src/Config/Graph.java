package Config;

import graph.Agent;
import graph.Topic;
import graph.TopicManagerSingleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * The {@code Graph} class represents a graph structure composed of {@link Node} instances.
 * It extends {@link ArrayList} to manage a collection of nodes and provides methods to
 * create the graph from topics, check for cycles, and print the graph.
 */
public class Graph extends ArrayList<Node> {
    /**
     * Checks if the graph contains any cycles. This method iterates through all nodes
     * in the graph and checks if any node has a cycle.
     *
     * @return {@code true} if the graph contains cycles, {@code false} otherwise.
     */
    public boolean hasCycles() {
        for (Node node : this) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a graph from the topics managed by the {@link TopicManagerSingleton}.
     * This method adds nodes representing topics and agents, and creates edges between
     * them based on the subscription and publishing relationships of the topics.
     */
    public void createFromTopics() {
        Node agentNode = null;
        HashMap<Agent, Node> createdAgents = new HashMap<Agent, Node>();
        Collection<Topic> topics = TopicManagerSingleton.get().getTopics();
        for (Topic topic : topics) {
            Node topicNode = new Node("T" + topic.name);
            this.add(topicNode);
            for (Agent agent : topic.getSubs()) {
                if (!createdAgents.containsKey(agent)) {
                    agentNode = new Node("A" + agent.getName());
                    this.add(agentNode);
                    createdAgents.put(agent, agentNode);
                    topicNode.addEdge(agentNode);
                } else {
                    topicNode.addEdge(createdAgents.get(agent));
                }
            }

            for (Agent agent : topic.getPubs()) {
                if (!createdAgents.containsKey(agent)) {
                    agentNode = new Node("A" + agent.getName());
                    agentNode.addEdge(topicNode);
                    createdAgents.put(agent, agentNode);
                    this.add(agentNode);
                } else {
                    createdAgents.get(agent).addEdge(topicNode);
                }
            }
        }
    }

    /**
     * Prints the graph to the console. For each node in the graph, this method prints the node's
     * name and the names of its connected edges.
     */
    public void printGraph() {
        for (Node node : this) {
            System.out.println("Node:" + node.getName());
            for (Node edge : node.getEdges()) {
                System.out.println("edge:" + edge.getName());
            }
        }
    }
}
