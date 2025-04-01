package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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
        SpawnPointProvider spawnPointProvider = contextProvider.getComponent(gameKey, SpawnPointProvider.class);

        GamePlayer gamePlayer = playerRegistry.findByEntity(player);
        Entity entity = gamePlayer.getEntity();

        if (!spawnPointProvider.hasSpawnPoint(entity.getUniqueId())) {
            return;
        }

        Location respawnLocation = spawnPointProvider.respawnEntity(entity);

        event.setRespawnLocation(respawnLocation);
    }
}
