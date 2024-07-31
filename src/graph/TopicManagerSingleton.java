package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code TopicManagerSingleton} class provides access to a singleton instance of the {@link TopicManager}.
 * This singleton manages a collection of {@link Topic} objects.
 */
public class TopicManagerSingleton {

    /**
     * Retrieves the singleton instance of {@link TopicManager}.
     *
     * @return The singleton {@link TopicManager} instance.
     */
    public static TopicManager get() {
        return TopicManager.instance;
    }

    /**
     * The {@code TopicManager} class is a singleton that manages a collection of {@link Topic} objects.
     * It provides methods to retrieve, search, and manage topics.
     */
    public static class TopicManager {

        /**
         * The singleton instance of {@link TopicManager}.
         */
        private static final TopicManager instance = new TopicManager();

        /**
         * A concurrent map storing topics with their names as keys.
         */
        ConcurrentHashMap<String, Topic> conMapTopic;

        /**
         * Private constructor to prevent instantiation from outside the class.
         */
        private TopicManager() {
            this.conMapTopic = new ConcurrentHashMap<String, Topic>();
        }

        /**
         * Retrieves a {@link Topic} by its name. If the topic does not exist, it is created and added to the collection.
         *
         * @param name The name of the topic to retrieve.
         * @return The {@link Topic} with the specified name.
         */
        public Topic getTopic(String name) {
            if (this.conMapTopic.containsKey(name)) {
                return this.conMapTopic.get(name);
            }
            Topic topic = new Topic(name);
            this.conMapTopic.put(name, topic);
            return topic;
        }

        /**
         * Checks if a topic with the specified name exists in the collection.
         *
         * @param name The name of the topic to search for.
         * @return {@code true} if the topic exists, {@code false} otherwise.
         */
        public boolean topicSearch(String name) {
            return this.conMapTopic.containsKey(name);
        }

        /**
         * Retrieves all topics managed by this {@link TopicManager}.
         *
         * @return A collection of all {@link Topic} objects.
         */
        public Collection<Topic> getTopics() {
            return new ArrayList<Topic>(this.conMapTopic.values());
        }

        /**
         * Clears all topics from the collection.
         */
        public void clear() {
            this.conMapTopic.clear();
        }
    }
}