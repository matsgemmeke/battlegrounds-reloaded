package nl.matsgemmeke.battlegrounds.configuration;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * Represents a set of plugin configurations stored inside a yaml file.
 */
public interface ConfigurationFile {

    /**
     * Creates an empty {@link ConfigurationSection} at the specified path.
     *
     * @param path the path to create the section at
     * @return the newly created section
     */
    ConfigurationSection createSection(String path);

    /**
     * Gets a string value from the configuration file. Returns an empty optional if the given path does not lead to a
     * value.
     *
     * @param path the path to the value
     * @return     an optional with the string value of the given path or empty if the path does not lead to a value
     */
    Optional<String> getString(String path);

    /**
     * Loads the contents from the configuration file into the configuration.
     */
    void load();

    /**
     * Save the configuration values into the configuration file.
     */
    void save();

    /**
     * Sets the specified path to the given value. If value is null, the entry will be removed. Any existing entry will
     * be replaced, regardless of what the new value is.
     *
     * @param path the path of the value to set
     * @param value the new value to set the path to
     */
    void set(String path, Object value);
}
