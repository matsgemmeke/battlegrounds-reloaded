package nl.matsgemmeke.battlegrounds.arena.configuration.setup;

import jakarta.validation.constraints.Past;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

import java.time.Instant;
import java.util.UUID;

public record ArenaMapData(
        @Required
        String name,
        @Past(message = "map creation date must be in the past")
        Instant createdAt,
        UUID createdBy
) {
}
