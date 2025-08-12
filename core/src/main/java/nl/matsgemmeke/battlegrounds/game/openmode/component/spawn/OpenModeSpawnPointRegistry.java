package nl.matsgemmeke.battlegrounds.game.openmode.component.spawn;

import nl.matsgemmeke.battlegrounds.game.GameScoped;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@GameScoped
public class OpenModeSpawnPointRegistry implements SpawnPointRegistry {

    @NotNull
    private final Map<UUID, SpawnPoint> customSpawnPointLocations;

    public OpenModeSpawnPointRegistry() {
        this.customSpawnPointLocations = new HashMap<>();
    }

    public Optional<SpawnPoint> getCustomSpawnPoint(UUID entityId) {
        return Optional.ofNullable(customSpawnPointLocations.get(entityId));
    }

    public boolean hasSpawnPoint(@NotNull UUID entityId) {
        return customSpawnPointLocations.containsKey(entityId);
    }

    public void setCustomSpawnPoint(@NotNull UUID entityId, @Nullable SpawnPoint spawnPoint) {
        customSpawnPointLocations.put(entityId, spawnPoint);
    }
}
