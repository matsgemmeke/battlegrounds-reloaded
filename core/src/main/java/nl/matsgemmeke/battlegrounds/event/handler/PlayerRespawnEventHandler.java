package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.registry.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerRespawnEventHandler implements EventHandler<PlayerRespawnEvent> {

    @NotNull
    private final GameContextProvider contextProvider;

    @Inject
    public PlayerRespawnEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GameKey gameKey = contextProvider.getGameKey(player);

        if (gameKey == null) {
            return;
        }

        PlayerRegistry playerRegistry = contextProvider.getComponent(gameKey, PlayerRegistry.class);
        GamePlayer gamePlayer = playerRegistry.findByEntity(player);
        SpawnPointProvider spawnPointProvider = contextProvider.getComponent(gameKey, SpawnPointProvider.class);

        if (!spawnPointProvider.hasSpawnPoint(gamePlayer)) {
            return;
        }

        Location respawnLocation = spawnPointProvider.respawnEntity(gamePlayer);

        event.setRespawnLocation(respawnLocation);
    }
}
