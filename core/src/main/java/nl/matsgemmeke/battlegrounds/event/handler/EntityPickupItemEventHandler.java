package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.item.ActionExecutorRegistry;
import nl.matsgemmeke.battlegrounds.item.action.ActionExecutor;
import nl.matsgemmeke.battlegrounds.item.action.PickupActionResult;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EntityPickupItemEventHandler implements EventHandler<EntityPickupItemEvent> {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<ActionExecutorRegistry> actionExecutorRegistryProvider;

    @Inject
    public EntityPickupItemEventHandler(GameContextProvider gameContextProvider, GameScope gameScope, Provider<ActionExecutorRegistry> actionExecutorRegistryProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.actionExecutorRegistryProvider = actionExecutorRegistryProvider;
    }

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

        gameScope.runInScope(gameContext, () -> this.performAction(event, player, itemStack));
    }

    private void performAction(EntityPickupItemEvent event, Player player, ItemStack itemStack) {
        ActionExecutorRegistry actionExecutorRegistry = actionExecutorRegistryProvider.get();
        ActionExecutor actionExecutor = actionExecutorRegistry.getActionExecutor(itemStack).orElse(null);

        if (actionExecutor == null) {
            return;
        }

        PickupActionResult result = actionExecutor.handlePickupAction(player, itemStack);

        if (result.removeItem()) {
            event.getItem().remove();
        }

        event.setCancelled(event.isCancelled() || !result.performAction());
    }
}
