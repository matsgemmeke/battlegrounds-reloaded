package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinEventHandler implements EventHandler<PlayerJoinEvent> {

    private static final GameKey OPEN_MODE_GAME_KEY = GameKey.ofOpenMode();

    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<PlayerLifecycleHandler> playerLifecycleHandlerProvider;

    @Inject
    public PlayerJoinEventHandler(@NotNull GameContextProvider gameContextProvider, @NotNull GameScope gameScope, @NotNull Provider<PlayerLifecycleHandler> playerLifecycleHandlerProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.playerLifecycleHandlerProvider = playerLifecycleHandlerProvider;
    }

    public void handle(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GameContext gameContext = gameContextProvider.getGameContext(OPEN_MODE_GAME_KEY)
                .orElseThrow(() -> new EventHandlingException("Unable to process PlayerJoinEvent for game key %s, no corresponding game context was found".formatted(OPEN_MODE_GAME_KEY)));

        gameScope.runInScope(gameContext, () -> {
            PlayerLifecycleHandler playerLifecycleHandler = playerLifecycleHandlerProvider.get();
            playerLifecycleHandler.handlePlayerJoin(player);
        });
    }
}
