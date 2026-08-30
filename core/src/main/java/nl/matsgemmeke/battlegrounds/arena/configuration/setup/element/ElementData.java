package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;

public abstract class ElementData {

    @NotNull(message = "element id is required")
    @Min(value = 1, message = "element id must be greater than zero")
    private Integer elementId;

    @NotNull
    @EnumValue(type = ElementType.class)
    private String elementType;

    public Integer getElementId() {
        return elementId;
    }

    public void setElementId(Integer elementId) {
        this.elementId = elementId;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }
}
