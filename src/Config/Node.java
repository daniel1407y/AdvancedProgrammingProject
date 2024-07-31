package Config;

import graph.Message;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;
    
    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public void addEdge(Node node){
        if (!edges.contains(node))
            edges.add(node);
    }

    public boolean hasCycles(){
        Set<Node> visited = new HashSet<>();
        Set<Node> curPath = new HashSet<>();
        return isCyclic(this, visited, curPath);
    }

    private boolean isCyclic(Node node, Set<Node> visited, Set<Node> curPath){
        if (curPath.contains(node)) // Current path already has the node
            return true;
        if (visited.contains(node)) // Already visited node, can stop iteration here
            return false;

        visited.add(node);
        curPath.add(node);

        for (Node neighbor : node.edges) {
            if (isCyclic(neighbor, visited, curPath))
                return true;
        }
        curPath.remove(node);
        return false;
    }
}