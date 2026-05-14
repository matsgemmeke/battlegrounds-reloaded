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
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerInteractEventHandler implements EventHandler<PlayerInteractEvent> {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<ItemInteractionDispatcher> itemInteractionDispatcherProvider;
    private final Provider<PlayerRegistry> playerRegistryProvider;

    @Inject
    public PlayerInteractEventHandler(
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
    public void handle(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        Action action = event.getAction();

        if (itemStack == null || action == Action.PHYSICAL) {
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

        gameScope.runInScope(gameContext, () -> this.performAction(event, playerId, itemStack, action));
    }

    private void performAction(PlayerInteractEvent event, UUID playerId, ItemStack itemStack, Action action) {
        PlayerRegistry playerRegistry = playerRegistryProvider.get();
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(playerId).orElse(null);

        if (gamePlayer == null) {
            return;
        }

        ItemInteractionDispatcher dispatcher = itemInteractionDispatcherProvider.get();
        DispatchResult result;

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            result = dispatcher.dispatchLeftClick(gamePlayer, itemStack);
        } else {
            result = dispatcher.dispatchRightClick(gamePlayer, itemStack);
        }

        if (result.cancelEvent()) {
            event.setUseItemInHand(Result.DENY);
        }
    }
}
