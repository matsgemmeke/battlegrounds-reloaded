package nl.matsgemmeke.battlegrounds.arena.configuration.setup.map;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ArenaMapData(
        @NotNull(message = "name is required")
        String name,
        @Past(message = "map creation date must be in the past")
        Instant createdAt,
        UUID createdBy,
        List<ElementData> elements
) {
}
