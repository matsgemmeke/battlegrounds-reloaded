package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface ItemActionHandler<T> {

    Optional<T> resolve(GamePlayer gamePlayer, ItemStack itemStack);

    DispatchResult dispatch(T item, GamePlayer gamePlayer, Action action);
}
