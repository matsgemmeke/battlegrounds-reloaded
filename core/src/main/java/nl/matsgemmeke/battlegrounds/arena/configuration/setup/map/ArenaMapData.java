package nl.matsgemmeke.battlegrounds.arena.configuration.setup.map;

import jakarta.validation.constraints.Past;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ArenaMapData(
        @Required
        String name,
        @Past(message = "map creation date must be in the past")
        Instant createdAt,
        UUID createdBy,
        List<ElementData> elements
) {
}
