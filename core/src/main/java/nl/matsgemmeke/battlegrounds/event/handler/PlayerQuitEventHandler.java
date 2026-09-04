package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitEventHandler implements EventHandler<PlayerQuitEvent> {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<PlayerLifecycleHandler> playerLifecycleHandlerProvider;

    @Inject
    public PlayerQuitEventHandler(GameContextProvider gameContextProvider, GameScope gameScope, Provider<PlayerLifecycleHandler> playerLifecycleHandlerProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.playerLifecycleHandlerProvider = playerLifecycleHandlerProvider;
    }

    public void handle(PlayerQuitEvent event) {
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
