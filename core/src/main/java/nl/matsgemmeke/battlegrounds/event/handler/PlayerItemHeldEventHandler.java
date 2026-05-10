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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class PlayerItemHeldEventHandler implements EventHandler<PlayerItemHeldEvent> {

    private static final ItemStack EMPTY_ITEM_STACK = new ItemStack(Material.AIR);

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<ItemInteractionDispatcher> itemInteractionDispatcherProvider;
    private final Provider<PlayerRegistry> playerRegistryProvider;

    @Inject
    public PlayerItemHeldEventHandler(
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
    public void handle(PlayerItemHeldEvent event) {
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
        PlayerRegistry playerRegistry = playerRegistryProvider.get();
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return;
        }

        ItemStack changeFromItemStack = player.getInventory().getItemInMainHand();
        ItemStack changeToItemStack = Optional.ofNullable(player.getInventory().getItem(event.getNewSlot())).orElse(EMPTY_ITEM_STACK);

        ItemInteractionDispatcher dispatcher = itemInteractionDispatcherProvider.get();
        DispatchResult changeFromResult = dispatcher.dispatchChangeFrom(gamePlayer, changeFromItemStack);
        DispatchResult changeToResult = dispatcher.dispatchChangeTo(gamePlayer, changeToItemStack);

        event.setCancelled(event.isCancelled() || changeFromResult.cancelEvent() || changeToResult.cancelEvent());
    }
}
