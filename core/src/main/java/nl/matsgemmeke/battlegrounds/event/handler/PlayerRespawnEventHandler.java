package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerRespawnEventHandler implements EventHandler<PlayerRespawnEvent> {

    @NotNull
    private GameContextProvider contextProvider;

    public PlayerRespawnEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GameContext context = contextProvider.getContext(player);

        if (context == null) {
            return;
        }

        GamePlayer gamePlayer = context.getPlayerRegistry().findByEntity(player);
        SpawnPointProvider spawnPointProvider = context.getSpawnPointProvider();

        if (!spawnPointProvider.hasSpawnPoint(gamePlayer)) {
            return;
        }

        Location respawnLocation = spawnPointProvider.respawnEntity(gamePlayer);

        event.setRespawnLocation(respawnLocation);
    }
}
