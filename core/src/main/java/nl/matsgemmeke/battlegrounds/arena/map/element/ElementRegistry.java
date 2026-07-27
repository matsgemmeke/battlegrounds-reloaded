package nl.matsgemmeke.battlegrounds.arena.map.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ElementRegistry {

    private final List<Element> elements;

    public ElementRegistry() {
        this.elements = new ArrayList<>();
    }

    public void addElement(Element element) {
        elements.add(element);
    }

    public int generateNextId() {
        Set<Integer> usedIds = elements.stream().map(Element::getId).collect(Collectors.toSet());
        int candidate = 1;

        while (usedIds.contains(candidate)) {
            candidate++;
        }

        return candidate;
    }
}
