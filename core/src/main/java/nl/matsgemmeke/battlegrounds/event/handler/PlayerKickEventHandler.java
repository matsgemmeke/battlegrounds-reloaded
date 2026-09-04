package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.UUID;

public class PlayerKickEventHandler implements EventHandler<PlayerKickEvent> {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<PlayerLifecycleHandler> playerLifecycleHandlerProvider;

    @Inject
    public PlayerKickEventHandler(GameContextProvider gameContextProvider, GameScope gameScope, Provider<PlayerLifecycleHandler> playerLifecycleHandlerProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.playerLifecycleHandlerProvider = playerLifecycleHandlerProvider;
    }

    public void handle(PlayerKickEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        GameContext gameContext = gameContextProvider.getGameContext(playerId).orElse(null);

        if (gameContext == null) {
            return;
        }

        gameScope.runInScope(gameContext, () -> {
            PlayerLifecycleHandler playerLifecycleHandler = playerLifecycleHandlerProvider.get();
            playerLifecycleHandler.handlePlayerLeave(playerId);
        });
    }
}
