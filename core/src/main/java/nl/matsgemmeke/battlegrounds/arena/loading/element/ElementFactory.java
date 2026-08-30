package nl.matsgemmeke.battlegrounds.arena.loading.element;

import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;
import nl.matsgemmeke.battlegrounds.arena.map.element.Element;

/**
 * Object that creates elements by converting their data object to a domain object.
 *
 * @param <T> the data object type
 * @param <S> the domain object type
 */
public interface ElementFactory<T extends ElementData, S extends Element> {

    /**
     * Creates a new instance of the domain object.
     *
     * @param data the data object
     * @return     a new domain object instance
     */
    S create(T data);
}
