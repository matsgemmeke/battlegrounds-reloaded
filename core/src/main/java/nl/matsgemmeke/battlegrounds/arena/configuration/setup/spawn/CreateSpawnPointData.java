package nl.matsgemmeke.battlegrounds.arena.configuration.setup.spawn;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;

public record CreateSpawnPointData(
        @NotNull(message = "a map name must be specified to create a spawn point")
        String mapName,
        @Min(value = 1, message = "element id must be greater than zero")
        int elementId,
        @NotNull(message = "location data must be specified to create a spawn point")
        @Valid
        LocationData locationData,
        @Min(value = 1, message = "team id must be greater than zero")
        int teamId
) {
}
