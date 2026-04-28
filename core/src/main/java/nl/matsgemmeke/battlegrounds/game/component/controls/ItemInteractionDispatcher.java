package nl.matsgemmeke.battlegrounds.game.component.controls;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.ItemInteractionHandler;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemInteractionDispatcher {

    private final Map<Action, List<ItemInteractionHandler<?>>> interactionHandlers;

    public ItemInteractionDispatcher() {
        this.interactionHandlers = new ConcurrentHashMap<>();
    }

    public <T> void registerActionHandler(Action action, ItemInteractionHandler<T> interactionHandler) {
        interactionHandlers.computeIfAbsent(action, k -> new ArrayList<>()).add(interactionHandler);
    }

    public DispatchResult dispatch(GamePlayer gamePlayer, ItemStack itemStack, Action action) {
        List<ItemInteractionHandler<?>> interactionHandlers = this.interactionHandlers.getOrDefault(action, Collections.emptyList());

        for (ItemInteractionHandler<?> interactionHandler : interactionHandlers) {
            DispatchResult result = this.dispatchTyped(interactionHandler, gamePlayer, itemStack, action);

            if (result.handled()) {
                return result;
            }
        }

        return DispatchResult.unhandled();
    }

    private <T> DispatchResult dispatchTyped(ItemInteractionHandler<T> interactionHandler, GamePlayer gamePlayer, ItemStack itemStack, Action action) {
        return interactionHandler.resolve(gamePlayer, itemStack)
                .map(item -> interactionHandler.dispatch(item, gamePlayer, action))
                .orElse(DispatchResult.unhandled());
    }
}
