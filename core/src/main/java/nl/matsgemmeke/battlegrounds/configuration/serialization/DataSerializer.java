package nl.matsgemmeke.battlegrounds.configuration.serialization;

import nl.matsgemmeke.battlegrounds.configuration.Section;

/**
 * An object that has the ability to serialize a data object into the plugin configuration.
 *
 * @param <T> the data object type
 */
public interface DataSerializer<T> {

    /**
     * Serializes the given data object into the given configuration section.
     *
     * @param data    the data object instance
     * @param section the section to serialize the data into
     */
    void serialize(T data, Section section);

    /**
     * Attempts to deserialize the data object type from a given configuration section.
     *
     * @throws SerializationException when an error occurs during the attempt to deserialize the data object
     */
    T deserialize(Section section);
}
