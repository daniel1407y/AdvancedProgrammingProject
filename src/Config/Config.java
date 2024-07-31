package Config;

import java.io.FileNotFoundException;

/**
 * The {@code Config} interface provides a contract for configuration management classes.
 * It defines methods for creating configurations, retrieving configuration details,
 * and closing resources associated with the configuration.
 */
public interface Config {
    /**
     * Creates the configuration based on the provided source.
     * This method may involve reading from a file or other sources to set up the configuration.
     *
     * @throws FileNotFoundException if the configuration file or source cannot be found.
     */
    void create() throws FileNotFoundException;

    /**
     * Returns the name of the configuration.
     *
     * @return a {@code String} representing the name of the configuration.
     */
    String getName();

    /**
     * Returns the version of the configuration.
     *
     * @return an {@code int} representing the version of the configuration.
     */
    int getVersion();

    /**
     * Closes and releases resources associated with the configuration.
     * This method is called to clean up resources when the configuration is no longer needed.
     */
    void close();
}