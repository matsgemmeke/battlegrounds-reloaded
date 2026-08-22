package nl.matsgemmeke.battlegrounds.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a section inside a configuration file.
 */
public interface Section {

    /**
     * Gets the absolute path where the section is located in its file.
     *
     * @return the section's absolute path
     */
    String getAbsolutePath();

    /**
     * Creates an empty section at the specified path.
     *
     * @param path the path to create the section at
     * @return     the newly created section
     */
    Section createSection(String path);

    /**
     * Gets whether a value exists at the given path.
     *
     * @param path the path to check
     * @return     whether the given exists in the section
     */
    boolean exists(String path);

    /**
     * Gets an integer value inside the section. Returns an empty optional if the given path does not lead to a value.
     *
     * @param path the path to the value
     * @return     an optional with the integer value of the given path or empty if the path does not lead to a value
     */
    Optional<Integer> getInt(String path);

    /**
     * Returns a set containing all keys in this section. The set contains only the keys of direct children.
     *
     * @return a set of keys in the section
     */
    Set<String> getKeys();

    /**
     * Gets an inner section from the section. Returns an empty optional if the given path does not lead to an inner
     * section.
     *
     * @param path the path to the section
     * @return     an optional with the inner section of the given path or empty if the path does not lead to an inner
     *             section
     */
    Optional<Section> getSection(String path);

    /**
     * Gets a string value inside the section. Returns an empty optional if the given path does not lead to a value.
     *
     * @param path the path to the value
     * @return     an optional with the string value of the given path or empty if the path does not lead to a value
     */
    Optional<String> getString(String path);

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
     * Removes an existing inner section.
     *
     * @param path the section path
     */
    void removeSection(String path);

    /**
     * Sets the specified path to the given value. If value is null, the entry will be removed. Any existing entry will
     * be replaced, regardless of what the new value is.
     *
     * @param path  the path of the value to set
     * @param value the new value to set the path to
     */
    void set(String path, Object value);
}
