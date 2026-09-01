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
}
