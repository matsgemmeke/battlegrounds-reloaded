package nl.matsgemmeke.battlegrounds.configuration;

import org.bukkit.Location;

import java.util.List;

/**
 * Represents a set of plugin configurations stored inside a yaml file.
 */
public interface ConfigurationFile {

    /**
     * Returns the section that sits at the root of the file
     *
     * @return the file's root section
     */
    Section getRootSection();

    /**
     * Gets the requested list of String by the given path. If the path does not exist, this will return an empty list.
     *
     * @param path the path to the list
     * @return     the list of String values at the given path
     */
    List<String> getStringList(String path);

    /**
     * Gets whether at the given path is a list. Always returns false when the given path does not exist.
     *
     * @param path the path to check
     * @return     whether the given path leads to a list
     */
    boolean isList(String path);

    /**
     * Removes an existing configuration section.
     *
     * @param path the section path
     */
    void removeSection(String path);

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
     * @param path  the path of the value to set
     * @param value the new value to set the path to
     */
    void set(String path, Object value);

    /**
     * Sets the specified path to the given location.
     *
     * @param path                      the path of the location
     * @param location                  the location
     * @throws IllegalArgumentException when the given location has no world
     */
    void setLocation(String path, Location location);
}
