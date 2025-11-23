package nl.matsgemmeke.battlegrounds.configuration;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a set of plugin configurations stored inside a yaml file.
 */
public interface PluginConfiguration {

    /**
     * Creates an empty {@link ConfigurationSection} at the specified path.
     *
     * @param path the path to create the section at
     * @return the newly created section
     */
    ConfigurationSection createSection(String path);

    /**
     * Gets an object from the configuration file. Returns null if the given path does not lead to a value in the
     * configuration file.
     *
     * @param path the path to the value
     * @return the value of the given path or null if the path does not lead to a value
     */
    @Nullable
    Object get(@NotNull String path);

    /**
     * Gets a boolean value from the configuration file. Returns null if the given path does not lead to a value in the
     * configuration file.
     *
     * @param path the path to the value
     * @return the value of the given path or null if the path does not lead to a value
     */
    boolean getBoolean(@NotNull String path);

    /**
     * Gets a boolean value from the configuration file. Returns the {@code def} parameter if the given path does not
     * lead to a value in the configuration file.
     *
     * @param path the path to the value
     * @param def the default value to return if the path is not found or is not a boolean
     * @return the value of the given path or the {@code def} parameter if the path does not lead to a value
     */
    boolean getBoolean(@NotNull String path, boolean def);

    /**
     * Gets a double value from the configuration file. Returns null if the given path does not lead to a value in the
     * configuration file.
     *
     * @param path the path to the value
     * @return the value of the given path or null if the path does not lead to a value
     */
    double getDouble(@NotNull String path);

    /**
     * Gets a double value from the configuration file. Returns the {@code def} parameter if the given path does not
     * lead to a value in the configuration file.
     *
     * @param path the path to the value
     * @param def the default value to return if the path is not found or is not a double
     * @return the value of the given path or the {@code def} parameter if the path does not lead to a value
     */
    double getDouble(@NotNull String path, double def);

    /**
     * Gets an integer value from the configuration file. Returns null if the given path does not lead to a value in the
     * configuration file.
     *
     * @param path the path to the value
     * @return the value of the given path or null if the path does not lead to a value
     */
    int getInteger(@NotNull String path);

    /**
     * Gets an integer value from the configuration file. Returns the {@code def} parameter if the given path does not
     * lead to a value in the configuration file.
     *
     * @param path the path to the value
     * @param def the default value to return if the path is not found or is not an integer
     * @return the value of the given path or the {@code def} parameter if the path does not lead to a value
     */
    int getInteger(@NotNull String path, int def);

    /**
     * Gets the root {@link Configuration} of the configuration file.
     *
     * @return the root section
     */
    Configuration getRoot();

    /**
     * Gets a {@link ConfigurationSection} value from configuration file. Returns null if the given path does not lead
     * to a value in the configuration file.
     *
     * @param path the path to the value
     * @return the value of the given path or null if the path does not lead to a value
     */
    @Nullable
    ConfigurationSection getSection(@NotNull String path);

    /**
     * Gets a string value from the configuration file. Returns null if the given path does not lead to a value in the
     * configuration file.
     *
     * @param path the path to the value
     * @return the value of the given path or null if the path does not lead to a value
     */
    @Nullable
    String getString(@NotNull String path);

    /**
     * Gets a string value from the configuration file. Returns the {@code def} parameter if the given path does not
     * lead to a value in the configuration file.
     *
     * @param path the path to the value
     * @param def the default value to return if the path is not found or is not a string
     * @return the value of the given path or the {@code def} parameter if the path does not lead to a value
     */
    @Contract("_, null -> null")
    @Nullable
    String getString(@NotNull String path, @Nullable String def);

    /**
     * Gets whether the file of this configuration is read-only and should not used to edit or delete values.
     *
     * @return whether the configuration is read-only
     */
    boolean isReadOnly();

    /**
     * Loads the contents from the configuration file into the configuration.
     *
     * @return whether the configuration file was able to be loaded correctly
     */
    boolean load();

    /**
     * Save the configuration values into the configuration file.
     *
     * @return whether the configuration was able to be saved correctly
     */
    boolean save();

    /**
     * Sets the specified path to the given value. If value is null, the entry will be removed. Any existing entry will
     * be replaced, regardless of what the new value is.
     *
     * @param path the path of the value to set
     * @param value the new value to set the path to
     */
    void setValue(@NotNull String path, @NotNull Object value);
}
