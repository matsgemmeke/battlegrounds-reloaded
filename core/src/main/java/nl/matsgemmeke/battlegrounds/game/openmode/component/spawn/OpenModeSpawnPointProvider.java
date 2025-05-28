package nl.matsgemmeke.battlegrounds.game.openmode.component.spawn;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.UUID;

public class OpenModeSpawnPointProvider implements SpawnPointProvider {

    @NotNull
    private SpawnPointStorage spawnPointStorage;

    @Inject
    public OpenModeSpawnPointProvider(@NotNull SpawnPointStorage spawnPointStorage) {
        this.spawnPointStorage = spawnPointStorage;
    }

    public boolean hasSpawnPoint(@NotNull UUID entityId) {
        return spawnPointStorage.getCustomSpawnPoint(entityId) != null;
    }

    @NotNull
    public Location respawnEntity(@NotNull Entity entity) {
        UUID entityId = entity.getUniqueId();
        SpawnPoint spawnPoint = spawnPointStorage.getCustomSpawnPoint(entityId);

        if (spawnPoint == null) {
            throw new IllegalStateException(MessageFormat.format("Cannot respawn entity {0} in open mode if it has no custom respawn location", entity.getName()));
        }

        spawnPoint.onSpawn(entity);
        spawnPointStorage.setCustomSpawnPoint(entityId, null);

        return spawnPoint.getLocation();
    }

    public void setCustomSpawnPoint(@NotNull UUID entityId, @Nullable SpawnPoint spawnPoint) {
        spawnPointStorage.setCustomSpawnPoint(entityId, spawnPoint);
    }
}
