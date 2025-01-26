package nl.matsgemmeke.battlegrounds.game.training.component.spawn;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

public class TrainingModeSpawnPointProvider implements SpawnPointProvider {

    @NotNull
    private SpawnPointStorage spawnPointStorage;

    @Inject
    public TrainingModeSpawnPointProvider(@Assisted @NotNull SpawnPointStorage spawnPointStorage) {
        this.spawnPointStorage = spawnPointStorage;
    }

    public boolean hasSpawnPoint(@NotNull GameEntity gameEntity) {
        return spawnPointStorage.getCustomSpawnPoint(gameEntity) != null;
    }

    @NotNull
    public Location respawnEntity(@NotNull GameEntity gameEntity) {
        SpawnPoint spawnPoint = spawnPointStorage.getCustomSpawnPoint(gameEntity);

        if (spawnPoint == null) {
            throw new IllegalStateException(MessageFormat.format("Cannot respawn entity {0} in training mode if it has no custom respawn location", gameEntity.getName()));
        }

        spawnPoint.onSpawn(gameEntity);
        spawnPointStorage.setCustomSpawnPoint(gameEntity, null);

        return spawnPoint.getLocation();
    }

    public void setCustomSpawnPoint(@NotNull GameEntity gameEntity, @Nullable SpawnPoint spawnPoint) {
        spawnPointStorage.setCustomSpawnPoint(gameEntity, spawnPoint);
    }
}
