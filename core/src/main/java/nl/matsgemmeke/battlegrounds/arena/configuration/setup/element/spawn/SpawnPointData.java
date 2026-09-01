package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;

public record SpawnPointData(
        @NotNull(message = "element id is required")
        @Min(value = 1, message = "element id must be greater than zero")
        Integer elementId,
        @NotNull
        @Valid
        LocationData locationData,
        @NotNull
        @Min(value = 1, message = "team id must be greater than zero")
        Integer teamId
) implements ElementData {

    @Override
    public String elementType() {
        return "SPAWN_POINT";
    }
}
