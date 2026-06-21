package nl.matsgemmeke.battlegrounds.game.freeplay.component.spawn;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.spawn.RespawnHandler;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import org.bukkit.Location;

import java.util.Optional;
import java.util.UUID;

public class FreeplayRespawnHandler implements RespawnHandler {

    private final SpawnPointRegistry spawnPointRegistry;

    @Inject
    public FreeplayRespawnHandler(SpawnPointRegistry spawnPointRegistry) {
        this.spawnPointRegistry = spawnPointRegistry;
    }

    @Override
    public Optional<Location> consumeRespawnLocation(UUID entityId) {
        return spawnPointRegistry.getCustomSpawnPoint(entityId).map(spawnPoint -> {
            spawnPoint.onSpawn();

            spawnPointRegistry.setCustomSpawnPoint(entityId, null);

            return spawnPoint.getLocation();
        });
    }
}
