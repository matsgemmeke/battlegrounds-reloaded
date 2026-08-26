package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element;

import nl.matsgemmeke.battlegrounds.configuration.Section;

import java.util.HashMap;
import java.util.Map;

public class ElementDataFactory {

    private final Map<ElementType, ElementDataReader> elementDataReaders;

    public ElementDataFactory() {
        this.elementDataReaders = new HashMap<>();
    }

    void addElementDataReader(ElementType elementType, ElementDataReader elementDataReader) {
        elementDataReaders.put(elementType, elementDataReader);
    }

    public ElementData create(ElementType elementType, Section section) {
        return elementDataReaders.get(elementType).read(section);
    }
}
