package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Topic} class represents a topic that can be subscribed to or published by {@link Agent} objects.
 * Each topic maintains a list of subscribers and publishers, as well as the last published {@link Message}.
 */
public class Topic {
    /**
     * The name of the topic.
     */
    public final String name;

    /**
     * A list of {@link Agent} objects that are subscribed to this topic.
     */
    List<Agent> subs;

    /**
     * A list of {@link Agent} objects that are publishers of this topic.
     */
    List<Agent> pubs;

    /**
     * The last {@link Message} published to this topic.
     */
    Message lastMessage;

    /**
     * Constructs a {@code Topic} with the specified name.
     * Initializes the lists for subscribers and publishers, and sets the last message to a new {@link Message} with an empty content.
     *
     * @param name The name of the topic.
     */
    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<Agent>();
        this.pubs = new ArrayList<Agent>();
        lastMessage = new Message("");
    }

    /**
     * Adds a {@link Agent} as a subscriber to this topic.
     *
     * @param a The {@link Agent} to subscribe.
     */
    public void subscribe(Agent a) {
        subs.add(a);
    }

    /**
     * Removes a {@link Agent} from the list of subscribers for this topic.
     *
     * @param a The {@link Agent} to unsubscribe.
     */
    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    /**
     * Publishes a {@link Message} to this topic.
     * The message is sent to all subscribers, and the last message is updated.
     *
     * @param m The {@link Message} to publish.
     */
    public void publish(Message m) {
        lastMessage = m;
        for (Agent sub : subs) {
            sub.callback(this.name, m);
        }
    }

    /**
     * Retrieves the last {@link Message} published to this topic.
     *
     * @return The last {@link Message} published to this topic.
     */
    public Message getLastMessage() {
        return lastMessage;
    }

    /**
     * Adds an {@link Agent} as a publisher for this topic.
     *
     * @param a The {@link Agent} to add as a publisher.
     */
    public void addPublisher(Agent a) {
        pubs.add(a);
    }

    /**
     * Removes an {@link Agent} from the list of publishers for this topic.
     *
     * @param a The {@link Agent} to remove as a publisher.
     */
    public void removePublisher(Agent a) {
        pubs.remove(a);
    }

    /**
     * Retrieves the list of {@link Agent} objects that are subscribed to this topic.
     *
     * @return A list of subscribers for this topic.
     */
    public List<Agent> getSubs() {
        return this.subs;
    }

    /**
     * Retrieves the list of {@link Agent} objects that are publishers of this topic.
     *
     * @return A list of publishers for this topic.
     */
    public List<Agent> getPubs() {
        return this.pubs;
    }
}
