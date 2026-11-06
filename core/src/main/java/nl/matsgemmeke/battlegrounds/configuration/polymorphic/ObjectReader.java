package nl.matsgemmeke.battlegrounds.configuration.polymorphic;

import nl.matsgemmeke.battlegrounds.configuration.Section;

/**
 * Reads a configuration {@link Section} and converts it into an object of type {@code T}.

 * @param <T> the type of object this reader produces from a configuration section
 */
public interface ObjectReader<T> {

    /**
     * Reads the given configuration section and converts it into an object of type {@code T}.
     *
     * @param section the configuration section to read from
     * @return        the object represented by the given section
     */
    T read(Section section);
}
