package nl.matsgemmeke.battlegrounds.game.component.controls;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.ItemActionHandler;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemInteractionDispatcher {

    private final Map<Action, List<ItemActionHandler<?>>> actionHandlers;

    public ItemInteractionDispatcher() {
        this.actionHandlers = new ConcurrentHashMap<>();
    }

    public <T> void registerActionHandler(Action action, ItemActionHandler<T> actionHandler) {
        actionHandlers.computeIfAbsent(action, k -> new ArrayList<>()).add(actionHandler);
    }

    public DispatchResult dispatch(GamePlayer gamePlayer, ItemStack itemStack, Action action) {
        List<ItemActionHandler<?>> actionHandlers = this.actionHandlers.getOrDefault(action, Collections.emptyList());

        for (ItemActionHandler<?> actionHandler : actionHandlers) {
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
