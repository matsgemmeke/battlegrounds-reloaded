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

import java.util.Optional;

public class EquipmentInteractionHandler implements ItemInteractionHandler<Equipment> {

    private final EquipmentRegistry equipmentRegistry;
    private final ItemControllerRegistry itemControllerRegistry;

    @Inject
    public EquipmentInteractionHandler(EquipmentRegistry equipmentRegistry, ItemControllerRegistry itemControllerRegistry) {
        this.equipmentRegistry = equipmentRegistry;
        this.itemControllerRegistry = itemControllerRegistry;
    }

    @Override
    public Optional<Equipment> resolve(GamePlayer gamePlayer, ItemStack itemStack) {
        return equipmentRegistry.getAssignedEquipment(gamePlayer, itemStack);
    }

    @Override
    public DispatchResult dispatch(Equipment equipment, GamePlayer gamePlayer, Action action) {
        ItemController<EquipmentUser> controller = itemControllerRegistry.getEquipmentController(equipment.getId()).orElse(null);

        if (controller == null) {
            return DispatchResult.unhandled();
        }

        ActionResult actionResult = controller.performActionNew(action, gamePlayer);
        return new DispatchResult(actionResult.performed(), actionResult.cancelEvent());
    }
}
