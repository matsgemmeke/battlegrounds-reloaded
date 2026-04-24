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
import java.util.UUID;

public class ActionDispatcher {

    private final List<ItemActionHandler<?>> actionHandlers;
    private final PlayerRegistry playerRegistry;

    @Inject
    public ActionDispatcher(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
        this.actionHandlers = new ArrayList<>();
    }

    public <T> void registerActionHandler(ItemActionHandler<T> actionHandler) {
        actionHandlers.add(actionHandler);
    }

    public DispatchResult dispatch(Player player, ItemStack itemStack, Action action) {
        UUID playerId = player.getUniqueId();
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(playerId).orElse(null);

        if (gamePlayer == null) {
            return DispatchResult.UNHANDLED;
        }

        for (ItemActionHandler<?> actionHandler : actionHandlers) {
            DispatchResult result = this.dispatchTyped(actionHandler, gamePlayer, itemStack, action);

            if (result != DispatchResult.UNHANDLED) {
                return result;
            }
        }

        return DispatchResult.UNHANDLED;
    }

    private <T> DispatchResult dispatchTyped(ItemActionHandler<T> handler, GamePlayer gamePlayer, ItemStack itemStack, Action action) {
        return handler.resolve(gamePlayer, itemStack)
                .map(item -> handler.dispatch(item, action))
                .orElse(DispatchResult.UNHANDLED);
    }
}
