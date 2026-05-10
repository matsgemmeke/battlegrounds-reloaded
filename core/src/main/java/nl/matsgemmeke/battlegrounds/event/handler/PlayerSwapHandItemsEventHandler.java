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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class PlayerSwapHandItemsEventHandler implements EventHandler<PlayerSwapHandItemsEvent> {

    private static final ItemStack EMPTY_ITEM_STACK = new ItemStack(Material.AIR);

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<ItemInteractionDispatcher> itemInteractionDispatcherProvider;
    private final Provider<PlayerRegistry> playerRegistryProvider;

    @Inject
    public PlayerSwapHandItemsEventHandler(
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
    public void handle(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        GameKey gameKey = gameContextProvider.getGameKeyByEntityId(playerId).orElse(null);

        if (gameKey == null) {
            return;
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey)
                .orElseThrow(() -> new EventHandlingException("Unable to process PlayerSwapHandItemsEvent for game key %s, no corresponding game context was found".formatted(gameKey)));

        gameScope.runInScope(gameContext, () -> this.performActions(event, playerId));
    }

    private void performActions(PlayerSwapHandItemsEvent event, UUID playerId) {
        PlayerRegistry playerRegistry = playerRegistryProvider.get();
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(playerId).orElse(null);

        if (gamePlayer == null) {
            return;
        }

        ItemStack swapFromItemStack = Optional.ofNullable(event.getOffHandItem()).orElse(EMPTY_ITEM_STACK);
        ItemStack swapToItemStack = Optional.ofNullable(event.getMainHandItem()).orElse(EMPTY_ITEM_STACK);

        ItemInteractionDispatcher dispatcher = itemInteractionDispatcherProvider.get();
        DispatchResult swapFromResult = dispatcher.dispatchSwapFrom(gamePlayer, swapFromItemStack);
        DispatchResult swapToResult = dispatcher.dispatchSwapTo(gamePlayer, swapToItemStack);

        event.setCancelled(event.isCancelled() || swapFromResult.cancelEvent() || swapToResult.cancelEvent());
    }
}
