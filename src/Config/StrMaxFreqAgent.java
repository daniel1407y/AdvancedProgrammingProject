package Config;

import java.util.*;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;
public class StrMaxFreqAgent implements Agent {

    private Map<Character,Integer> values;
    private List<String> subNames;
    private List<String> pubNames;
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    public StrMaxFreqAgent(String[] subs ,String[] pubs)
    {
        subNames = new ArrayList<>();
        pubNames = new  ArrayList<>();
        values = new HashMap<Character,Integer>();
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
        return "StrMaxFreqAgent";
    }

    @Override
    public void reset() {
        values.clear();
    }

    @Override
    public void callback(String topic, Message msg) {
        for (char c : msg.asText.toCharArray()){
            values.put(c, values.getOrDefault(c, 0) + 1);
        }

        int maxValue = 0;
        char maxChar = ' '; // to keep track of the character with the maximum value
        for (Map.Entry<Character, Integer> entry : values.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                maxChar = entry.getKey();
            }
        }
        
        for (String pub : pubNames){
            topicManager.getTopic(pub).publish(new Message(Character.toString(maxChar)));
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
