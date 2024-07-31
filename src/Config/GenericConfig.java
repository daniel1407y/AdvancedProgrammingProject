package Config;

import graph.Agent;
import graph.ParallelAgent;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The {@code GenericConfig} class implements the {@link Config} interface and provides
 * functionality for loading and configuring {@link ParallelAgent} instances from a configuration file.
 * This class handles reading the configuration file, creating agents based on the file's content,
 * and managing the lifecycle of these agents.
 */
public class GenericConfig implements Config {

    private String confFile;
    private ArrayList<ParallelAgent> parallelAgents;

    /**
     * Constructs a {@code GenericConfig} instance.
     */
    public GenericConfig() {
        parallelAgents = new ArrayList<ParallelAgent>();
    }

    /**
     * Sets the path to the configuration file.
     *
     * @param confFile the path to the configuration file.
     */
    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

    /**
     * Reads the configuration file, parses its content, and creates {@link ParallelAgent} instances
     * based on the file's content. Each agent is initialized and added to the
     * {@code parallelAgents} list. If the file format is incorrect or any error occurs during
     * instantiation, an {@code IllegalArgumentException} is thrown.
     *
     * @throws IllegalArgumentException if the file format is incorrect or any instantiation error occurs.
     */
    @Override
    public void create() throws IllegalArgumentException {
        List<String> lines = this.readFile();
        String[] subs;
        String[] pubs;
        Agent agent;


        if (lines.size() % 3 != 0 || lines.isEmpty()) {
            throw new IllegalArgumentException("Bad File Format");
        }

        for (int i = 0; i < lines.size(); i = i + 3) {
            try {
                Class<?> classType = Class.forName(lines.get(i));
                Constructor<?> constructor = classType.getConstructor(String[].class, String[].class);
                subs = lines.get(i + 1).split(",");
                pubs = lines.get(i + 2).split(",");
                agent = (Agent) constructor.newInstance(subs, pubs);
                parallelAgents.add(new ParallelAgent(agent, 20));
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Class Does Not Exists");
            }
        }
    }

    /**
     * Reads the configuration file and returns its content as a list of strings.
     *
     * @return a list of strings representing the lines of the configuration file.
     */
    private List<String> readFile() {
        try {
            return Files.readAllLines(Paths.get(confFile));
        } catch (IOException e) {
            System.out.println("File does not exist");
        }
        return new ArrayList<String>();
    }

    /**
     * Returns the name of the configuration. This implementation returns {@code null}.
     *
     * @return {@code null}
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * Returns the version of the configuration. This implementation returns {@code 0}.
     *
     * @return {@code 0}
     */
    @Override
    public int getVersion() {
        return 0;
    }

    /**
     * Closes all {@link ParallelAgent} instances managed by this configuration.
     * This method called to release resources when the configuration is no longer needed.
     */
    @Override
    public void close() {
        for (ParallelAgent agent : parallelAgents) {
            agent.close();
        }
    }
}
