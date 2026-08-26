package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element;

import nl.matsgemmeke.battlegrounds.configuration.Section;

/**
 * Object responsible for reading configuration sections and transforming its data into {@link ElementData} instances.
 */
public interface ElementDataReader {

    /**
     * Creates a new element data object from the values found in the given section.
     *
     * @param section the section to read the values from
     * @return        an element data instance containing values from the section
     */
    ElementData read(Section section);
}
