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
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerInteractEventHandler implements EventHandler<PlayerInteractEvent> {

    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<ActionExecutorRegistry> actionExecutorRegistryProvider;

    @Inject
    public PlayerInteractEventHandler(
            @NotNull GameContextProvider gameContextProvider,
            @NotNull GameScope gameScope,
            @NotNull Provider<ActionExecutorRegistry> actionExecutorRegistryProvider
    ) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.actionExecutorRegistryProvider = actionExecutorRegistryProvider;
    }

    public void handle(@NotNull PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        GameKey gameKey = gameContextProvider.getGameKeyByEntityId(playerId).orElse(null);

        if (gameKey == null) {
            return;
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey)
                .orElseThrow(() -> new EventHandlingException("Unable to process PlayerInteractEvent for game key %s, no corresponding game context was found".formatted(gameKey)));

        gameScope.runInScope(gameContext, () -> this.performAction(event, player, itemStack));
    }

    private void performAction(PlayerInteractEvent event, Player player, ItemStack itemStack) {
        ActionExecutorRegistry actionExecutorRegistry = actionExecutorRegistryProvider.get();
        ActionExecutor actionExecutor = actionExecutorRegistry.getActionExecutor(itemStack).orElse(null);

        if (actionExecutor == null) {
            return;
        }

        Action action = event.getAction();
        boolean actionPerformed = true;

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            actionPerformed = actionExecutor.handleLeftClickAction(player, itemStack);
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            actionPerformed = actionExecutor.handleRightClickAction(player, itemStack);
        }

        if (!actionPerformed) {
            event.setUseItemInHand(Result.DENY);
        }
    }
}
