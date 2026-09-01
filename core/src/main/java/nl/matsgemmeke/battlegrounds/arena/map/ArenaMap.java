package nl.matsgemmeke.battlegrounds.arena.map;

import nl.matsgemmeke.battlegrounds.arena.map.element.Element;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaMap {

    private final ArenaMapMetadata metadata;
    private final List<Element> elements;
    private final Map<Class<? extends Element>, List<Element>> elementsByType;
    private final String name;

    public ArenaMap(String name, ArenaMapMetadata metadata) {
        this.name = name;
        this.metadata = metadata;
        this.elements = new ArrayList<>();
        this.elementsByType = new HashMap<>();
    }

    public List<Element> getElements() {
        return elements;
    }

    public ArenaMapMetadata getMetadata() {
        return metadata;
    }

    public String getName() {
        return name;
    }

    public void addElement(Element element) {
        elements.add(element);
        elementsByType.computeIfAbsent(element.getClass(), elements -> new ArrayList<>()).add(element);
    }

    public int generateNextElementId() {
        Set<Integer> usedIds = elements.stream().map(Element::getId).collect(Collectors.toSet());
        int candidate = 1;

        while (usedIds.contains(candidate)) {
            candidate++;
        }

        return candidate;
    }
}
