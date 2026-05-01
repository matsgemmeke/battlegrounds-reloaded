package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import org.bukkit.inventory.ItemStack;

public interface ItemInteractionHandler {

    DispatchResult handleInteraction(GamePlayer gamePlayer, ItemStack itemStack, Action action);
}
