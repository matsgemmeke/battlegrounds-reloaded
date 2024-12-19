package nl.matsgemmeke.battlegrounds.game.spawn;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SpawnPointStorage {

    @NotNull
    private Map<GameEntity, SpawnPoint> customSpawnPointLocations;

    public SpawnPointStorage() {
        this.customSpawnPointLocations = new HashMap<>();
    }

    @Nullable
    public SpawnPoint getCustomSpawnPoint(@NotNull GameEntity gameEntity) {
        return customSpawnPointLocations.get(gameEntity);
    }

    public void setCustomSpawnPoint(@NotNull GameEntity gameEntity, @Nullable SpawnPoint spawnPoint) {
        customSpawnPointLocations.put(gameEntity, spawnPoint);
    }
}
