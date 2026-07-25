package nl.matsgemmeke.battlegrounds.arena.map;

import java.time.Instant;
import java.util.UUID;

public record ArenaMapMetadata(Instant createdAt, UUID createdBy) {
}
