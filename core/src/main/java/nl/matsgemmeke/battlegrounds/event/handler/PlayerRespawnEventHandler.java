package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.spawn.RespawnHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerRespawnEventHandler implements EventHandler<PlayerRespawnEvent> {

    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<RespawnHandler> respawnHandlerProvider;

    @Inject
    public PlayerRespawnEventHandler(@NotNull GameContextProvider gameContextProvider, @NotNull GameScope gameScope, @NotNull Provider<RespawnHandler> respawnHandlerProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.respawnHandlerProvider = respawnHandlerProvider;
    }

    public void handle(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        GameKey gameKey = gameContextProvider.getGameKeyByEntityId(playerId).orElse(null);

        if (gameKey == null) {
            return;
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey)
                .orElseThrow(() -> new IllegalStateException("Unable to process PlayerRespawnEvent for game key %s, no corresponding game context was found".formatted(gameKey)));

        gameScope.runInScope(gameContext, () -> {
            RespawnHandler respawnHandler = respawnHandlerProvider.get();
            respawnHandler.consumeRespawnLocation(playerId).ifPresent(event::setRespawnLocation);
        });
    }
}
