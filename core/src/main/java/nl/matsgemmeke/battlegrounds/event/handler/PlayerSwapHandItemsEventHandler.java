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
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class PlayerSwapHandItemsEventHandler implements EventHandler<PlayerSwapHandItemsEvent> {

    private static final ItemStack EMPTY_ITEM_STACK = new ItemStack(Material.AIR);

    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<ActionExecutorRegistry> actionExecutorRegistryProvider;

    @Inject
    public PlayerSwapHandItemsEventHandler(
            @NotNull GameContextProvider gameContextProvider,
            @NotNull GameScope gameScope,
            @NotNull Provider<ActionExecutorRegistry> actionExecutorRegistryProvider
    ) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.actionExecutorRegistryProvider = actionExecutorRegistryProvider;
    }

    public void handle(@NotNull PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        GameKey gameKey = gameContextProvider.getGameKeyByEntityId(playerId).orElse(null);

        if (gameKey == null) {
            return;
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey)
                .orElseThrow(() -> new EventHandlingException("Unable to process PlayerSwapHandItemsEvent for game key %s, no corresponding game context was found".formatted(gameKey)));

        gameScope.runInScope(gameContext, () -> this.performActions(event, player));
    }

    private void performActions(PlayerSwapHandItemsEvent event, Player player) {
        ItemStack swapFrom = Optional.ofNullable(event.getOffHandItem()).orElse(EMPTY_ITEM_STACK);
        ItemStack swapTo = Optional.ofNullable(event.getMainHandItem()).orElse(EMPTY_ITEM_STACK);

        boolean performSwapFromAction = this.performAction(swapFrom, actionExecutor -> actionExecutor.handleSwapFromAction(player, swapFrom));
        boolean performSwapToAction = this.performAction(swapTo, actionExecutor -> actionExecutor.handleSwapToAction(player, swapTo));

        event.setCancelled(event.isCancelled() || !performSwapFromAction || !performSwapToAction);
    }

    private boolean performAction(ItemStack itemStack, Function<ActionExecutor, Boolean> actionExecutorFunction) {
        if (itemStack.getType().isAir()) {
            return true;
        }

        ActionExecutorRegistry actionExecutorRegistry = actionExecutorRegistryProvider.get();
        ActionExecutor actionExecutor = actionExecutorRegistry.getActionExecutor(itemStack).orElse(null);

        if (actionExecutor == null) {
            return true;
        }

        return actionExecutorFunction.apply(actionExecutor);
    }
}
