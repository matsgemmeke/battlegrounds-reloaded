package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionResult;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class GunInteractionHandler implements ItemInteractionHandler {

    private final GunRegistry gunRegistry;
    private final ItemControllerRegistry itemControllerRegistry;

    @Inject
    public GunInteractionHandler(GunRegistry gunRegistry, ItemControllerRegistry itemControllerRegistry) {
        this.gunRegistry = gunRegistry;
        this.itemControllerRegistry = itemControllerRegistry;
    }

    @Override
    public DispatchResult handleChangeFrom(GamePlayer gamePlayer, ItemStack itemStack) {
        BiConsumer<Gun, ItemController<GunUser>> consumer = (gun, controller) -> controller.cancelAllFunctions();

        return this.handleInteraction(gamePlayer, itemStack, Action.CHANGE_FROM, consumer);
    }

    @Override
    public DispatchResult handleChangeTo(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.CHANGE_TO);
    }

    @Override
    public DispatchResult handleDropItem(GamePlayer gamePlayer, ItemStack itemStack) {
        BiConsumer<Gun, ItemController<GunUser>> consumer = (gun, controller) -> {
            gun.setUser(null);
            controller.cancelAllFunctions();
        };

        return this.handleInteraction(gamePlayer, itemStack, Action.DROP_ITEM, consumer);
    }

    @Override
    public DispatchResult handleLeftClick(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.LEFT_CLICK);
    }

    @Override
    public DispatchResult handlePickupItem(GamePlayer gamePlayer, ItemStack itemStack) {
        BiConsumer<Gun, ItemController<GunUser>> consumer = (gun, controller) -> gun.setUser(gamePlayer);

        return this.handleInteraction(gamePlayer, itemStack, Action.PICKUP_ITEM, consumer);
    }

    @Override
    public DispatchResult handleRightClick(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.RIGHT_CLICK);
    }

    @Override
    public DispatchResult handleSwapFrom(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.SWAP_FROM);
    }

    @Override
    public DispatchResult handleSwapTo(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.SWAP_TO);
    }

    private DispatchResult handleInteraction(GamePlayer gamePlayer, ItemStack itemStack, Action action) {
        return this.handleInteraction(gamePlayer, itemStack, action, (gun, controller) -> {});
    }

    private DispatchResult handleInteraction(GamePlayer gamePlayer, ItemStack itemStack, Action action, BiConsumer<Gun, ItemController<GunUser>> consumer) {
        Gun gun = gunRegistry.getAssignedGun(gamePlayer, itemStack).orElse(null);

        if (gun == null) {
            return DispatchResult.unhandled();
        }

        ItemController<GunUser> controller = itemControllerRegistry.getGunController(gun.getId()).orElse(null);

        if (controller == null) {
            return DispatchResult.unhandled();
        }

        ActionResult actionResult = controller.performActionNew(action, gamePlayer);

        consumer.accept(gun, controller);

        return new DispatchResult(actionResult.performed(), actionResult.cancelEvent());
    }
}
