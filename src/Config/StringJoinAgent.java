package Config;

import java.util.*;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;
public class StringJoinAgent implements Agent {

    private Map<String,String> values;
    private List<String> subNames;
    private List<String> pubNames;
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    public StringJoinAgent(String[] subs ,String[] pubs)
    {
        subNames = new ArrayList<>();
        pubNames = new  ArrayList<>();
        values = new HashMap<String,String>();
        for (String sub : subs){
            topicManager.getTopic(sub).subscribe(this);
            subNames.add(sub);
        }
        for (String pub : pubs){
            topicManager.getTopic(pub).addPublisher(this);
            pubNames.add(pub);
        }
    }


    @Override
    public String getName() {
        return "StringJoinAgent";
    }

    @Override
    public void reset() {
        values.clear();
    }

    @Override
    public void callback(String topic, Message msg) {
        values.put(topic,msg.asText);
        StringBuilder mergedString = new StringBuilder();
        
        for (String sub : subNames) {
            if (values.containsKey(sub)) {
                mergedString.append(values.get(sub));
            }
        }

        for (String pub : pubNames){
            topicManager.getTopic(pub).publish(new Message(mergedString.toString()));
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
