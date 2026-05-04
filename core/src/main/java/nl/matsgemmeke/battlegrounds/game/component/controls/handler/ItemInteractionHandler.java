package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import org.bukkit.inventory.ItemStack;

public interface ItemInteractionHandler {

    DispatchResult handleChangeFrom(GamePlayer gamePlayer, ItemStack itemStack);

    DispatchResult handleChangeTo(GamePlayer gamePlayer, ItemStack itemStack);

    DispatchResult handleDropItem(GamePlayer gamePlayer, ItemStack itemStack);

    DispatchResult handleLeftClick(GamePlayer gamePlayer, ItemStack itemStack);

    DispatchResult handlePickupItem(GamePlayer gamePlayer, ItemStack itemStack);

    DispatchResult handleRightClick(GamePlayer gamePlayer, ItemStack itemStack);

    DispatchResult handleSwapFrom(GamePlayer gamePlayer, ItemStack itemStack);

    DispatchResult handleSwapTo(GamePlayer gamePlayer, ItemStack itemStack);
}
