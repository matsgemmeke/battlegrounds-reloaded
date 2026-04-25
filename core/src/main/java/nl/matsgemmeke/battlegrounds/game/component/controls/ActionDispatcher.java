package nl.matsgemmeke.battlegrounds.game.component.controls;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.ItemActionHandler;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ActionDispatcher {

    private final Map<Action, List<ItemActionHandler<?>>> actionHandlers;
    private final PlayerRegistry playerRegistry;

    @Inject
    public ActionDispatcher(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
        this.actionHandlers = new ConcurrentHashMap<>();
    }

    public <T> void registerActionHandler(Action action, ItemActionHandler<T> actionHandler) {
        actionHandlers.computeIfAbsent(action, k -> new ArrayList<>()).add(actionHandler);
    }

    public DispatchResult dispatch(Player player, ItemStack itemStack, Action action) {
        UUID playerId = player.getUniqueId();
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(playerId).orElse(null);

        if (gamePlayer == null) {
            return DispatchResult.unhandled();
        }

        for (ItemActionHandler<?> actionHandler : actionHandlers.get(action)) {
            DispatchResult result = this.dispatchTyped(actionHandler, gamePlayer, itemStack, action);

            if (result.handled()) {
                return result;
            }
        }

        return DispatchResult.unhandled();
    }

    private <T> DispatchResult dispatchTyped(ItemActionHandler<T> handler, GamePlayer gamePlayer, ItemStack itemStack, Action action) {
        return handler.resolve(gamePlayer, itemStack)
                .map(item -> handler.dispatch(item, gamePlayer, action))
                .orElse(DispatchResult.unhandled());
    }
}
