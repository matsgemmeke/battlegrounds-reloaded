package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitEventHandler implements EventHandler<PlayerQuitEvent> {

    @NotNull
    private final GameContextProvider contextProvider;

    @Inject
    public PlayerQuitEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameKey gameKey = contextProvider.getGameKey(player);

        if (gameKey == null) {
            return;
        }

        PlayerLifecycleHandler playerLifecycleHandler = contextProvider.getComponent(gameKey, PlayerLifecycleHandler.class);
        playerLifecycleHandler.handlePlayerLeave(player.getUniqueId());
    }
}
