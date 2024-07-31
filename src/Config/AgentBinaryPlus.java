package Config;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;

public class AgentBinaryPlus implements Agent {
    private String xName;
    private String yName;
    private Topic outputTopic;
    private String name;
    private String x;
    private String y;
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
    public AgentBinaryPlus(String[] subs ,String[] pubs)
    {
        x = "1";
        y = "1";
        subToTopics(subs);
        outputTopic = topicManager.getTopic(pubs[0]);
        outputTopic.addPublisher(this);
    }

    private void subToTopics(String[] subs) {
        topicManager.getTopic(subs[0]).subscribe(this);
        xName = topicManager.getTopic(subs[0]).name;
        topicManager.getTopic(subs[1]).subscribe(this);
        yName = topicManager.getTopic(subs[1]).name;
    }

    @Override
    public String getName() {
        return "AgentBinaryPlus";
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void reset() {
        x = "1";
        y = "1";
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(xName))
            x = msg.asText;
        if (topic.equals(yName))
            y = msg.asText;
        outputTopic.publish(new Message(x +"+"+ y));
    }

    @Override
    public void close() {
        topicManager.getTopic(xName).unsubscribe(this);
        topicManager.getTopic(yName).unsubscribe(this);
        outputTopic.removePublisher(this);
    }
}
