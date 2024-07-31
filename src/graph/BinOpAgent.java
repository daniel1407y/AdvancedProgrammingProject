package graph;

import java.util.function.BinaryOperator;
import graph.TopicManagerSingleton.TopicManager;

public class BinOpAgent implements Agent {
    private String name;
    private String topic1Name;
    private String topic2Name;
    private Double topic1Value;
    private Double topic2Value;
    private Topic resultsTopic;

    private BinaryOperator<Double> binaryOperator;

    public BinOpAgent(String name, String topic1Name, String topic2Name, String resultTopicName, BinaryOperator<Double> binaryOperator) {
        this.name = name;
        this.topic1Name = topic1Name;
        this.topic2Name = topic2Name;
        this.topic1Value = Double.NaN;
        this.topic2Value = Double.NaN;
        this.binaryOperator = binaryOperator;

        TopicManager topicManager = TopicManagerSingleton.get();
        topicManager.getTopic(topic1Name).subscribe(this);
        topicManager.getTopic(topic2Name).subscribe(this);
        this.resultsTopic = topicManager.getTopic(resultTopicName);
        resultsTopic.addPublisher(this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void reset() {
        this.topic1Value = 0.0;
        this.topic2Value = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(topic1Name)) {
            topic1Value = msg.asDouble;
        } else if (topic.equals(topic2Name)) {
            topic2Value = msg.asDouble;
        } else {
            return;
        }

        if (!topic1Value.isNaN() && !topic2Value.isNaN()) {
            Double result = binaryOperator.apply(topic1Value, topic2Value);
            resultsTopic.publish(new Message(result));
        }
    }

    /**
     * Unsubscribes this agent from the input topics and the result topic.
     */
    @Override
    public void close() {
        TopicManager topicManager = TopicManagerSingleton.get();
        topicManager.getTopic(topic1Name).unsubscribe(this);
        topicManager.getTopic(topic2Name).unsubscribe(this);
        resultsTopic.unsubscribe(this);
    }
}