package nl.matsgemmeke.battlegrounds.game.openmode.component.spawn;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameScoped;
import nl.matsgemmeke.battlegrounds.game.component.spawn.RespawnHandler;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

@GameScoped
public class OpenModeRespawnHandler implements RespawnHandler {

    @NotNull
    private final SpawnPointRegistry spawnPointRegistry;

    @Inject
    public OpenModeRespawnHandler(@NotNull SpawnPointRegistry spawnPointRegistry) {
        this.spawnPointRegistry = spawnPointRegistry;
    }

    public Optional<Location> consumeRespawnLocation(UUID entityId) {
        return spawnPointRegistry.getCustomSpawnPoint(entityId).map(spawnPoint -> {
            spawnPoint.onSpawn();

            spawnPointRegistry.setCustomSpawnPoint(entityId, null);

            return spawnPoint.getLocation();
        });
    }
}
