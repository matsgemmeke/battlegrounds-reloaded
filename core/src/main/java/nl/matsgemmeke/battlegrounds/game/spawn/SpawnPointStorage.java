package nl.matsgemmeke.battlegrounds.game.spawn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnPointStorage {

    @NotNull
    private Map<UUID, SpawnPoint> customSpawnPointLocations;

    public SpawnPointStorage() {
        this.customSpawnPointLocations = new HashMap<>();
    }

    @Nullable
    public SpawnPoint getCustomSpawnPoint(@NotNull UUID entityId) {
        return customSpawnPointLocations.get(entityId);
    }

    public void setCustomSpawnPoint(@NotNull UUID entityId, @Nullable SpawnPoint spawnPoint) {
        customSpawnPointLocations.put(entityId, spawnPoint);
    }
}
