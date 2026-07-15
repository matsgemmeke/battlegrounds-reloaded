package nl.matsgemmeke.battlegrounds.arena.configuration;

import java.time.Instant;
import java.util.UUID;

public record MapCreationInfo(String mapName, Instant createdAt, UUID createdBy) {
}
