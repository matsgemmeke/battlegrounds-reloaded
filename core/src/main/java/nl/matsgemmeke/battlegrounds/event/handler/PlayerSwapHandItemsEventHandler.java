package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.component.item.ActionInvoker;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PlayerSwapHandItemsEventHandler implements EventHandler<PlayerSwapHandItemsEvent> {

    private static final ItemStack EMPTY_ITEM_STACK = new ItemStack(Material.AIR);

    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<ActionInvoker> actionInvokerProvider;

    @Inject
    public PlayerSwapHandItemsEventHandler(@NotNull GameContextProvider gameContextProvider, @NotNull GameScope gameScope, @NotNull Provider<ActionInvoker> actionInvokerProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.actionInvokerProvider = actionInvokerProvider;
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
        ActionInvoker actionInvoker = actionInvokerProvider.get();
        ItemStack swapFrom = Optional.ofNullable(event.getOffHandItem()).orElse(EMPTY_ITEM_STACK);
        ItemStack swapTo = Optional.ofNullable(event.getMainHandItem()).orElse(EMPTY_ITEM_STACK);

        boolean performSwapFromAction = actionInvoker.performAction(swapFrom, actionExecutor -> actionExecutor.handleSwapFromAction(player, swapFrom));
        boolean performSwapToAction = actionInvoker.performAction(swapTo, actionExecutor -> actionExecutor.handleSwapToAction(player, swapTo));

        event.setCancelled(event.isCancelled() || !performSwapFromAction || !performSwapToAction);
    }
}
