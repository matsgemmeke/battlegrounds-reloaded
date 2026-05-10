package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemInteractionDispatcher;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerDropItemEventHandler implements EventHandler<PlayerDropItemEvent> {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<ItemInteractionDispatcher> itemInteractionDispatcherProvider;
    private final Provider<PlayerRegistry> playerRegistryProvider;

    @Inject
    public PlayerDropItemEventHandler(
            GameContextProvider gameContextProvider,
            GameScope gameScope,
            Provider<ItemInteractionDispatcher> itemInteractionDispatcherProvider,
            Provider<PlayerRegistry> playerRegistryProvider
    ) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.itemInteractionDispatcherProvider = itemInteractionDispatcherProvider;
        this.playerRegistryProvider = playerRegistryProvider;
    }

    @Override
    public void handle(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        GameKey gameKey = gameContextProvider.getGameKeyByEntityId(playerId).orElse(null);

        if (gameKey == null) {
            return;
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey)
                .orElseThrow(() -> new EventHandlingException("Unable to process PlayerDropItemEvent for game key %s, no corresponding game context was found".formatted(gameKey)));

        gameScope.runInScope(gameContext, () -> this.performAction(event, playerId));
    }

    private void performAction(PlayerDropItemEvent event, UUID playerId) {
        PlayerRegistry playerRegistry = playerRegistryProvider.get();
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(playerId).orElse(null);

        if (gamePlayer == null) {
            return;
        }

        ItemStack itemStack = event.getItemDrop().getItemStack();
        ItemInteractionDispatcher dispatcher = itemInteractionDispatcherProvider.get();
        DispatchResult result = dispatcher.dispatchDropItem(gamePlayer, itemStack);

        event.setCancelled(event.isCancelled() || result.cancelEvent());
    }
}
