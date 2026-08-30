package nl.matsgemmeke.battlegrounds.arena.loading.element;

import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;

public record UnsupportedElementData() implements ElementData {

    @Override
    public Integer elementId() {
        return 0;
    }

    @Override
    public String elementType() {
        return "";
    }
}
