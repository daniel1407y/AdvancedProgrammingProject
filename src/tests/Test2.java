package tests;

import graph.*;
import server.MyHTTPServer;
import servlets.ConfLoader;
import servlets.HtmlLoader;
import servlets.TopicDisplayer;
import graph.TopicManagerSingleton.TopicManager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;


//the given test tested every aspect so we didn't add much
public class Test2 {
    static String tn=null;

    public static class TestAgent3 implements Agent{

        public void reset() {
        }
        public void close() {
        }
        public String getName(){
            return getClass().getName();
        }

        @Override
        public void callback(String topic, Message msg) {
            tn=Thread.currentThread().getName();
        }

    }

    public static void testParallel() throws InterruptedException
    {
        TopicManager tm=TopicManagerSingleton.get();
        int tc=Thread.activeCount();
        ParallelAgent agent1=new ParallelAgent(new TestAgent3(), 10);
        ParallelAgent agent2=new ParallelAgent(new TestAgent3(), 10);
        tm.getTopic("A").subscribe(agent1);

        if (Thread.activeCount()!=tc+2){
            System.out.println("your ParallelAgents does not open a thread (-10)");
        }
        agent2.close();
        Thread.sleep(1000);
        if (Thread.activeCount()!=tc+1)
            System.out.println("your ParallelAgents does not close a thread when done(-10)");
        tm.getTopic("A").publish(new Message("a"));
        try { Thread.sleep(100);} catch (InterruptedException e) {}
        if(tn==null){
            System.out.println("your ParallelAgent didn't run the wrapped agent callback (-20)");
        }else{
            if(tn.equals(Thread.currentThread().getName())){
                System.out.println("the ParallelAgent does not run the wrapped agent in a different thread (-10)");
            }
            String last=tn;
            tm.getTopic("A").publish(new Message("a"));
            try { Thread.sleep(100);} catch (InterruptedException e) {}
            if(!last.equals(tn))
                System.out.println("all messages should be processed in the same thread of ParallelAgent (-10)");
        }
        agent1.close();
        System.out.println("done");
    }


    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        testParallel();
        System.out.println("done");
    }


}