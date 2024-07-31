package Config;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;

public class ReverseAgent implements Agent {

    String name;
    private String xName;
    private Topic outputTopic;
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    public ReverseAgent(String[] subs, String[] pubs) {
        topicManager.getTopic(subs[0]).subscribe(this);
        xName = topicManager.getTopic(subs[0]).name;
        outputTopic = topicManager.getTopic(pubs[0]);
        outputTopic.addPublisher(this);
        this.name = "ReverseAgent";
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
    }

    @Override
    public void callback(String topic, Message msg) {
        StringBuilder sb = new StringBuilder(msg.asText);
        String reversed = sb.reverse().toString();
        outputTopic.publish(new Message(reversed));
    }

    @Override
    public void close() {
        topicManager.getTopic(xName).unsubscribe(this);
        outputTopic.removePublisher(this);
    }
}