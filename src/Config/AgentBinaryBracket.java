package Config;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;

public class AgentBinaryBracket implements Agent{
    private String name;
    private String xName;
    private Topic outputTopic;
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    public AgentBinaryBracket(String[] subs ,String[] pubs)
    {
        topicManager.getTopic(subs[0]).subscribe(this);
        xName = topicManager.getTopic(subs[0]).name;
        outputTopic = topicManager.getTopic(pubs[0]);
        outputTopic.addPublisher(this);
        this.name = "AgentBracketBinary";
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
        outputTopic.publish(new Message("("+msg.asText+")"));
    }

    @Override
    public void close() {
        topicManager.getTopic(xName).unsubscribe(this);
        outputTopic.removePublisher(this);
    }
}
