package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;

public interface ElementData {

    @NotNull(message = "element id is required")
    @Min(value = 1, message = "element id must be greater than zero")
    Integer elementId();

    @NotNull
    @EnumValue(type = ElementType.class)
    String elementType();
}
