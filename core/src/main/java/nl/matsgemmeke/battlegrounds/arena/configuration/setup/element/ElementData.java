package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public abstract class ElementData {

    @NotNull
    @Min(value = 1, message = "element id must be greater than zero")
    private Integer elementId;

    public Integer getElementId() {
        return elementId;
    }

    public void setElementId(Integer elementId) {
        this.elementId = elementId;
    }
}
