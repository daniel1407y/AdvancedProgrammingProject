package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The {@code ParallelAgent} class is a wrapper around an {@link Agent} that processes messages in parallel.
 * It uses a separate thread to handle message processing from a blocking queue.
 */
public class ParallelAgent implements Agent {
    private final Agent agent;
    private final BlockingQueue<Message> bq;
    private final Thread t1;

    /**
     * Constructs a {@code ParallelAgent} with the specified {@link Agent} and a queue capacity.
     * Initializes the blocking queue and starts a separate thread to process messages.
     *
     * @param agent The {@link Agent} to be wrapped and processed in parallel.
     * @param capacity The capacity of the blocking queue used to hold messages.
     */
    public ParallelAgent(Agent agent, int capacity) {
        this.agent = agent;
        this.bq = new ArrayBlockingQueue<>(capacity);

        this.t1 = new Thread(() -> {
            while (true) {
                try {
                    // Take and process messages from the queue
                    String fullMessage = bq.take().asText;
                    String[] parts = fullMessage.split("%%%%%");
                    String topic = parts[1];
                    agent.callback(topic, new Message(parts[0]));
                } catch (InterruptedException e) {
                    // Exit the loop if interrupted
                    break;
                }
            }
        });
        t1.start();
    }

    /**
     * Returns the name of the wrapped {@link Agent}.
     *
     * @return The name of the wrapped {@link Agent}.
     */
    @Override
    public String getName() {
        return agent.getName();
    }

    /**
     * Resets the wrapped {@link Agent}.
     */
    @Override
    public void reset() {
        agent.reset();
    }

    /**
     * Enqueues a message for processing. The message is formatted with the topic and message content,
     * and added to the blocking queue.
     *
     * @param topic The topic associated with the message.
     * @param msg The {@link Message} to enqueue.
     */
    @Override
    public void callback(String topic, Message msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(topic).append("%%%%%").append(msg.asText);
        try {
            bq.put(new Message(sb.toString()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interrupts the processing thread and closes the wrapped {@link Agent}.
     */
    @Override
    public void close() {
        t1.interrupt();
        agent.close();
    }
}