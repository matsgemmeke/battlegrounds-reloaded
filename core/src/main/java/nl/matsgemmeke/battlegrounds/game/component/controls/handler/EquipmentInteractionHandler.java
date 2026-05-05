package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionResult;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class EquipmentInteractionHandler implements ItemInteractionHandler {

    private final EquipmentRegistry equipmentRegistry;
    private final ItemControllerRegistry itemControllerRegistry;

    @Inject
    public EquipmentInteractionHandler(EquipmentRegistry equipmentRegistry, ItemControllerRegistry itemControllerRegistry) {
        this.equipmentRegistry = equipmentRegistry;
        this.itemControllerRegistry = itemControllerRegistry;
    }

    @Override
    public DispatchResult handleChangeFrom(GamePlayer gamePlayer, ItemStack itemStack) {
        BiConsumer<Equipment, ItemController<EquipmentUser>> consumer = (equipment, controller) -> controller.cancelAllFunctions();

        return this.handleInteraction(gamePlayer, itemStack, Action.CHANGE_FROM, consumer);
    }

    @Override
    public DispatchResult handleChangeTo(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.CHANGE_TO);
    }

    @Override
    public DispatchResult handleDropItem(GamePlayer gamePlayer, ItemStack itemStack) {
        BiConsumer<Equipment, ItemController<EquipmentUser>> consumer = (equipment, controller) -> {
            controller.cancelAllFunctions();
            equipment.setUser(null);
        };

        return this.handleInteraction(gamePlayer, itemStack, Action.DROP_ITEM, consumer);
    }

    @Override
    public DispatchResult handleLeftClick(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.LEFT_CLICK);
    }

    @Override
    public DispatchResult handlePickupItem(GamePlayer gamePlayer, ItemStack itemStack) {
        BiConsumer<Equipment, ItemController<EquipmentUser>> consumer = (equipment, controller) -> equipment.setUser(gamePlayer);

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
        return this.handleInteraction(gamePlayer, itemStack, action, (equipment, controller) -> {});
    }

    private DispatchResult handleInteraction(GamePlayer gamePlayer, ItemStack itemStack, Action action, BiConsumer<Equipment, ItemController<EquipmentUser>> consumer) {
        Equipment equipment = equipmentRegistry.getAssignedEquipment(gamePlayer, itemStack).orElse(null);

        if (equipment == null) {
            return DispatchResult.unhandled();
        }

        ItemController<EquipmentUser> controller = itemControllerRegistry.getEquipmentController(equipment.getId()).orElse(null);

        if (controller == null) {
            return DispatchResult.unhandled();
        }

        ActionResult actionResult = controller.performActionNew(action, gamePlayer);

        consumer.accept(equipment, controller);

        return new DispatchResult(actionResult.performed(), actionResult.cancelEvent());
    }
}
