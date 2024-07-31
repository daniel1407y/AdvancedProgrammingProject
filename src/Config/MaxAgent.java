package Config;

import java.util.*;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
public class MaxAgent implements Agent {

    private String name;
    private Map<String,Double> values;
    private List<String> subNames;
    private List<String> pubNames;
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    public MaxAgent(String[] subs ,String[] pubs)
    {
        this.name = "MaxAgent";
        subNames = new ArrayList<>();
        pubNames = new  ArrayList<>();
        values = new HashMap<String,Double>();
        for (String sub : subs){
            topicManager.getTopic(sub).subscribe(this);
            subNames.add(sub);
        }
        for (String pub : pubs){
            topicManager.getTopic(pub).addPublisher(this);
            pubNames.add(pub);
        }
    }

    public void addSub(String topic)
    {
        subNames.add(topic);
    }

    public void addPub(String topic)
    {
        pubNames.add(topic);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public void reset() {
        values.clear();
    }

    @Override
    public void callback(String topic, Message msg) {
        values.put(topic,msg.asDouble);
        double max = Collections.max(values.values());
        for (String pub : pubNames){
            topicManager.getTopic(pub).publish(new Message(max));
        }
    }


    @Override
    public void close() {
        for (String sub: subNames){
            topicManager.getTopic(sub).unsubscribe(this);
        }
        for (String pub: pubNames){
            topicManager.getTopic(pub).unsubscribe(this);
        }
    }
}
