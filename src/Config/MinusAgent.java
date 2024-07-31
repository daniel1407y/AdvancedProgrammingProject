package Config;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;

public class MinusAgent implements Agent {
    private String xName;
    private String yName;
    private Topic outputTopic;
    private String name;
    private Double x;
    private Double y;
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
    public MinusAgent(String[] subs ,String[] pubs)
    {
        this.name = "MinusAgent";
        x = 0.0;
        y = 0.0;
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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public void reset() {
        x = 0.0;
        y = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(xName) && !Double.isNaN(msg.asDouble))
            x = msg.asDouble;
        if (topic.equals(yName) && !Double.isNaN(msg.asDouble))
            y = msg.asDouble;
        outputTopic.publish(new Message(x-y));
    }

    @Override
    public void close() {
        topicManager.getTopic(xName).unsubscribe(this);
        topicManager.getTopic(yName).unsubscribe(this);
        outputTopic.removePublisher(this);
    }
}
