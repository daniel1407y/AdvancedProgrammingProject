package tests;

import Config.Graph;
import Config.MathExampleConfig;
import Config.Node;
import graph.*;
import server.MyHTTPServer;
import servlets.ConfLoader;
import servlets.HtmlLoader;
import servlets.TopicDisplayer;
import graph.TopicManagerSingleton.TopicManager;
import Config.testConfig;
import java.io.*;
import java.net.Socket;
import java.util.*;
import Config.Config;

public class Test3 {


    public static boolean hasCycles(List<Node> graph) {
        for (Node node : graph) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    //testing a more complex graph than the given test
    public static void testCycles(){
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("E");

        a.addEdge(b);
        b.addEdge(c);
        a.addEdge(d);
        d.addEdge(e);
        e.addEdge(c);


        // Create a graph
        List<Node> graph = new ArrayList<>();
        graph.add(a);
        graph.add(b);
        graph.add(c);
        graph.add(d);
        graph.add(e);

        // Check if the graph has cycles
        boolean hasCycles = hasCycles(graph);
        if(hasCycles)
            System.out.println("wrong answer for hasCycles when there are no cycles (-20)");

        c.addEdge(a);
        hasCycles = hasCycles(graph);
        if(!hasCycles)
            System.out.println("wrong answer for hasCycles when there is a cycle (-10)");

    }

    public static class GetAgent implements Agent{

        public Message msg;
        public GetAgent(String topic){
            TopicManagerSingleton.get().getTopic(topic).subscribe(this);
        }

        @Override
        public String getName() { return "Get Agent";}

        public void setName(String name) {

        }

        @Override
        public void reset() {}

        @Override
        public void callback(String topic, Message msg) {
            this.msg=msg;
        }

        @Override
        public void close() {}

    }
    //test different configs
    public static void testBinGraph() throws FileNotFoundException {
        TopicManager tm=TopicManagerSingleton.get();
        tm.clear();
        Config c= new testConfig();
        c.create();
        GetAgent ga=new GetAgent("R3");
        tm.getTopic("A").publish(new Message(4));
        tm.getTopic("B").publish(new Message(2));
        if (ga.msg.asDouble != 36)
            System.out.println("your BinOpAgents did not produce the desired result (-20)");
    }
    //a similar test but with higher difficulty
    public static void testTopicsGraph() throws FileNotFoundException {
        TopicManager tm=TopicManagerSingleton.get();
        tm.clear();
        Config c=new testConfig();
        c.create();
        Graph g=new Graph();
        g.createFromTopics();

        if(g.size()!=10)
            System.out.println("the graph you created from topics is not in the right size (-10)");

        List<String> l= Arrays.asList("TA","TB","Aplus","Aminus","TR1","TR2","Apow","TR3","Adiv","TR4");
        boolean b=true;
        for(Node n  : g){
            b&=l.contains(n.getName());
        }
        if(!b)
            System.out.println("the graph you created from topics has wrong names to Nodes (-10)");

        if (g.hasCycles())
            System.out.println("Wrong result in hasCycles for topics graph without cycles (-10)");

        GetAgent ga=new GetAgent("R3");
        tm.getTopic("A").addPublisher(ga); // cycle
        g.createFromTopics();

        if (!g.hasCycles())
            System.out.println("Wrong result in hasCycles for topics graph with a cycle (-10)");
    }
    public static void main(String[] args) throws FileNotFoundException {
        testCycles();
        testBinGraph();
        testTopicsGraph();
        System.out.println("done");
    }
}
