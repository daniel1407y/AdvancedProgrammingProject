package graph;

import java.util.Date;

/**
 * The {@code Message} class represents a message with different representations:
 * text, byte array, double, and date. It provides constructors to create messages
 * from various data types and converts the content to these different representations.
 */
public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(String content) {
        this.data = content.getBytes();
        this.asText = new String(content);
        double temp;
        try {
            temp = Double.parseDouble(content);
        } catch (NumberFormatException e) {
            temp = Double.NaN;
        }
        this.asDouble = temp;
        this.date = new Date();
    }

    public Message(byte[] content) {
        this(new String(content));
    }

    public Message(Double content) {
        this(Double.toString(content));
    }

    public Message(Integer content) {
        this(Integer.toString(content));
    }
}
