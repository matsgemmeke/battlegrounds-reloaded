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
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemInteractionDispatcher;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EntityPickupItemEventHandler implements EventHandler<EntityPickupItemEvent> {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<ItemInteractionDispatcher> itemInteractionDispatcherProvider;
    private final Provider<PlayerRegistry> playerRegistryProvider;

    @Inject
    public EntityPickupItemEventHandler(
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
    public void handle(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        UUID playerId = player.getUniqueId();
        GameKey gameKey = gameContextProvider.getGameKeyByEntityId(playerId).orElse(null);

        if (gameKey == null) {
            return;
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey)
                .orElseThrow(() -> new EventHandlingException("Unable to process EntityPickupItemEvent for game key %s, no corresponding game context was found".formatted(gameKey)));
        ItemStack itemStack = event.getItem().getItemStack();

        gameScope.runInScope(gameContext, () -> this.performAction(event, playerId, itemStack));
    }

    private void performAction(EntityPickupItemEvent event, UUID playerId, ItemStack itemStack) {
        PlayerRegistry playerRegistry = playerRegistryProvider.get();
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(playerId).orElse(null);

        if (gamePlayer == null) {
            return;
        }

        ItemInteractionDispatcher dispatcher = itemInteractionDispatcherProvider.get();
        DispatchResult result = dispatcher.dispatch(gamePlayer, itemStack, Action.PICKUP_ITEM);

        if (result.handled()) {
            event.getItem().remove();
        }

        event.setCancelled(event.isCancelled() || result.cancelEvent());
    }
}
