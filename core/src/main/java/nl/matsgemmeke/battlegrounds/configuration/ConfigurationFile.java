package nl.matsgemmeke.battlegrounds.configuration;

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
}
