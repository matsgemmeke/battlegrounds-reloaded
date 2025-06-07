package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinEventHandler implements EventHandler<PlayerJoinEvent> {

    private static final GameKey OPEN_MODE_GAME_KEY = GameKey.ofOpenMode();

    @NotNull
    private final GameContextProvider contextProvider;

    @Inject
    public PlayerJoinEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerLifecycleHandler playerLifecycleHandler = contextProvider.getComponent(OPEN_MODE_GAME_KEY, PlayerLifecycleHandler.class);
        playerLifecycleHandler.handlePlayerJoin(player);
    }
}
