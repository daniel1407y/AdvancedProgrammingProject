package Config;

import BinaryCalc.Q3;
import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;

public class AgentBinaryExpression implements Agent{
    private String name;
    private String xName;
    private Topic outputTopic;
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    public AgentBinaryExpression(String[] subs ,String[] pubs)
    {
        topicManager.getTopic(subs[0]).subscribe(this);
        xName = topicManager.getTopic(subs[0]).name;
        outputTopic = topicManager.getTopic(pubs[0]);
        outputTopic.addPublisher(this);
        this.name = "AgentBinaryExpression";
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
        try{
            outputTopic.publish(new Message(Q3.calc(msg.asText)));
        } catch (Exception e) {
        }
    }

    @Override
    public void close() {
        topicManager.getTopic(xName).unsubscribe(this);
        outputTopic.removePublisher(this);
    }
}

