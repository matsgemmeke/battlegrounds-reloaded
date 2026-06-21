package nl.matsgemmeke.battlegrounds.game.component.spawn;

import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DefaultSpawnPointRegistry implements SpawnPointRegistry {

    private final Map<UUID, SpawnPoint> customSpawnPointLocations;

    public DefaultSpawnPointRegistry() {
        this.customSpawnPointLocations = new HashMap<>();
    }

    @Override
    public Optional<SpawnPoint> getCustomSpawnPoint(UUID entityId) {
        return Optional.ofNullable(customSpawnPointLocations.get(entityId));
    }

    @Override
    public void setCustomSpawnPoint(UUID entityId, SpawnPoint spawnPoint) {
        customSpawnPointLocations.put(entityId, spawnPoint);
    }
}
