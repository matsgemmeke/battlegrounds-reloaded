package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.event.action.ActionInvoker;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PlayerItemHeldEventHandler implements EventHandler<PlayerItemHeldEvent> {

    private static final ItemStack EMPTY_ITEM_STACK = new ItemStack(Material.AIR);

    @NotNull
    private final ActionInvoker actionInvoker;
    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;

    @Inject
    public PlayerItemHeldEventHandler(
            @NotNull ActionInvoker actionInvoker,
            @NotNull GameContextProvider gameContextProvider,
            @NotNull GameScope gameScope
    ) {
        this.actionInvoker = actionInvoker;
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
    }

    public void handle(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        GameKey gameKey = gameContextProvider.getGameKeyByEntityId(playerId).orElse(null);

        if (gameKey == null) {
            return;
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey)
                .orElseThrow(() -> new EventHandlingException("Unable to process PlayerItemHeldEvent for game key %s, no corresponding game context was found".formatted(gameKey)));

        gameScope.runInScope(gameContext, () -> this.performActions(event, player));
    }

    private void performActions(PlayerItemHeldEvent event, Player player) {
        ItemStack changeFrom = player.getInventory().getItemInMainHand();
        ItemStack changeTo = Optional.ofNullable(player.getInventory().getItem(event.getNewSlot())).orElse(EMPTY_ITEM_STACK);

        boolean performChangeFromAction = actionInvoker.performAction(changeFrom, actionExecutor -> actionExecutor.handleChangeFromAction(player, changeFrom));
        boolean performChangeToAction = actionInvoker.performAction(changeTo, actionExecutor -> actionExecutor.handleChangeToAction(player, changeTo));

        event.setCancelled(event.isCancelled() || !performChangeFromAction || !performChangeToAction);
    }
}
