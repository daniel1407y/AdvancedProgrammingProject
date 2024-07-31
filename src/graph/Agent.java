package graph;

/**
 * The {@code Agent} interface represents a component that can subscribe to topics, receive messages,
 * and perform operations based on those messages.
 */
public interface Agent {

    /**
     * Returns the name of this agent.
     *
     * @return The name of this agent.
     */
    String getName();
    /**
     * Resets the internal state of this agent. This method is intended to initialize or clear
     * any data held by the agent, preparing it for new operations or restarting its state.
     */

    void reset();

    /**
     * Processes the received message. This method is called when a message is published to a topic
     * that the agent is subscribed to.
     *
     * @param topic The name of the topic from which the message was received.
     * @param msg The message received from the topic.
     */
    void callback(String topic, Message msg);

    /**
     * Unsubscribes the agent from all topics and performs any necessary cleanup. This method
     * is typically called when the agent is no longer needed or is being shut down.
     */
    void close();
}