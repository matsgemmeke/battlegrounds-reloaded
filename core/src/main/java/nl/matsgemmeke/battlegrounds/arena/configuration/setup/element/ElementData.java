package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element;

import jakarta.validation.constraints.Min;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public abstract class ElementData {

    @Min(value = 1, message = "element id must be greater than zero")
    private int elementId;

    @Required(message = "elements must have a type declaration")
    private ElementType elementType;

    public int getElementId() {
        return elementId;
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }
}
