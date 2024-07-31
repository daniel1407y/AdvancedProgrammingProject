package tests;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import java.util.Objects;
import java.io.FileNotFoundException;
import java.util.List;

public class Test1 {
    //testing every constructor of message
    public static void testMessage(){
        // Test String constructor
        String testString= "123.123";
        Message msgFromString= new Message(testString);
        if (!testString.equals(msgFromString.asText)) {
            System.out.println("Error: String constructor - asText does not match input string (-5)");
        }
        if (!java.util.Arrays.equals(testString.getBytes(), msgFromString.data)) {
            System.out.println("Error: String constructor - data does not match input string bytes (-5)");
        }
        if (msgFromString.asDouble != Double.parseDouble(testString)) {
            System.out.println("Error: String constructor - asDouble not match input double(-5)");
        }
        if (msgFromString.date == null) {
            System.out.println("Error: String constructor - date should not be null (-5)");
        }

        // Test Double constructor
        Message msgFromDouble = new Message(123.456);
        if (!testString.equals(msgFromString.asText)) {
            System.out.println("Error: Double constructor - asText does not match input string (-5)");
        }
        if (!java.util.Arrays.equals(testString.getBytes(), msgFromString.data)) {
            System.out.println("Error: Double constructor - data does not match input string bytes (-5)");
        }
        if (msgFromString.asDouble != Double.parseDouble(testString)) {
            System.out.println("Error: Double constructor - asDouble not match input double(-5)");
        }
        if (msgFromString.date == null) {
            System.out.println("Error: Double constructor - date should not be null (-5)");
        }

        //Test Byte[] constructor
        Message msgFromByte = new Message("1452".getBytes());
        if (!testString.equals(msgFromString.asText)) {
            System.out.println("Error: Byte[] constructor - asText does not match input string (-5)");
        }
        if (!java.util.Arrays.equals(testString.getBytes(), msgFromString.data)) {
            System.out.println("Error: Byte[] constructor - data does not match input string bytes (-5)");
        }
        if (msgFromString.asDouble != Double.parseDouble(testString)) {
            System.out.println("Error: Byte[] constructor - asDouble not match input double(-5)");
        }
        if (msgFromString.date == null) {
            System.out.println("Error: Byte[] constructor - date should not be null (-5)");
        }
        System.out.println("Done testing Message");
    }

    public static  abstract class AAgent implements Agent {
        public void reset() {}
        public void close() {}
        public String getName(){
            return getClass().getName();
        }
    }

    public static class TestAgent1 extends AAgent{

        double sum=0;
        int count=0;
        TopicManager tm= TopicManagerSingleton.get();

        public TestAgent1(){
            tm.getTopic("testTopic1").subscribe(this);
            tm.getTopic("testTopic2").subscribe(this);
            tm.getTopic("resultTopic").addPublisher(this);
        }

        @Override
        public void callback(String topic, Message msg) {
            String toSend = topic+" "+this.getName()+" "+msg.asText;
            tm.getTopic("resultTopic").publish(new Message(toSend));
        }
    }
    public static class TestAgent2 extends AAgent{

        double sum=0;
        String topicPub;
        TopicManager tm=TopicManagerSingleton.get();

        public TestAgent2(String topicSub,String topicPub){
            tm.getTopic(topicPub).addPublisher(this);
            tm.getTopic(topicSub).subscribe(this);
            this.topicPub = topicPub;
        }

        @Override
        public void callback(String topic, Message msg) {
            if (!Double.isNaN(msg.asDouble))
                sum=msg.asDouble+1;
            tm.getTopic(topicPub).publish(new Message(sum));
        }

        public double getSum(){
            return sum;
        }
    }


    public static void testAgentAndTopic()
    {
        //testing every aspect of agent and topic, checking the ability to subscribe and publish
        //testing callback operation
        TopicManager tm=TopicManagerSingleton.get();
        Topic topicTest1 = tm.getTopic("testTopic1");
        Topic topicTest2 = tm.getTopic("testTopic2");
        Topic resultTopic = tm.getTopic("resultTopic");
        TestAgent1 a = new TestAgent1();
        TestAgent2 a2=new TestAgent2(topicTest1.name,resultTopic.name);
        if (!Objects.equals(topicTest1.getSubs().get(0), a))
            System.out.println("Error: Failed to Add subscriber");
        if(!Objects.equals(resultTopic.getPubs().get(0), a))
            System.out.println("Error: Failed to Add publisher");
        topicTest1.publish(new Message(20));
        if (a2.getSum() != 21)
            System.out.println("Error: Callout failed");
        a.close();
        a2.close();
        System.out.println("Done testing Agent and Topic");
    }


    public static void main(String[] args) throws FileNotFoundException {
        testMessage();
        testAgentAndTopic();
        System.out.println("done");
    }
    

}

