package nl.matsgemmeke.battlegrounds.arena.configuration.setup.spawn;

import jakarta.validation.constraints.Min;
import nl.matsgemmeke.battlegrounds.validation.constraint.HasWorld;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;
import org.bukkit.Location;

public record CreateSpawnPointData(
        @Required
        String mapName,
        @Min(value = 1, message = "element id must be greater than zero")
        int elementId,
        @Required
        @HasWorld
        Location location,
        @Min(value = 1, message = "team id must be greater than zero")
        int teamId
) {
}
