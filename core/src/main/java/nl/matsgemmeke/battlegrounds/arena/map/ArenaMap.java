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

    public void removeElement(Element element) {
        elements.remove(element);

        List<Element> typeList = elementsByType.get(element.getClass());

        if (typeList != null) {
            typeList.remove(element);
            elementsByType.remove(element.getClass());
        }
    }

    public boolean elementExists(int elementId) {
        return elements.stream().anyMatch(element -> element.getId() == elementId);
    }

    public Optional<Element> getElement(int elementId) {
        return elements.stream().filter(element -> element.getId() == elementId).findFirst();
    }

    public List<Integer> getElementIds() {
        return elements.stream().map(Element::getId).toList();
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
