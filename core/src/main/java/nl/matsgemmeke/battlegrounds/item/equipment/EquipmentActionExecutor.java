package nl.matsgemmeke.battlegrounds.item.equipment;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EquipmentActionExecutor implements ActionExecutor {

    private final EquipmentRegistry equipmentRegistry;
    private final PlayerRegistry playerRegistry;

    @Inject
    public EquipmentActionExecutor(EquipmentRegistry equipmentRegistry, PlayerRegistry playerRegistry) {
        this.equipmentRegistry = equipmentRegistry;
        this.playerRegistry = playerRegistry;
    }

    @Override
    public boolean handleChangeFromAction(Player player, ItemStack changedItem) {
        Equipment equipment = this.getAssignedEquipment(player, changedItem);

        if (equipment == null) {
            return true;
        }

        equipment.onChangeFrom();
        return true;
    }

    @Override
    public boolean handleChangeToAction(Player player, ItemStack changedItem) {
        Equipment equipment = this.getAssignedEquipment(player, changedItem);

        if (equipment == null) {
            return true;
        }

        equipment.onChangeTo();
        return true;
    }

    @Override
    public boolean handleDropItemAction(Player player, ItemStack droppedItem) {
        Equipment equipment = this.getAssignedEquipment(player, droppedItem);

        if (equipment == null) {
            return true;
        }

        equipmentRegistry.unassign(equipment);
        equipment.onDrop();
        return true;
    }

    @Override
    public boolean handleLeftClickAction(Player player, ItemStack clickedItem) {
        Equipment equipment = this.getAssignedEquipment(player, clickedItem);

        if (equipment == null) {
            return true;
        }

        equipment.onLeftClick();
        return false;
    }

    @Override
    public boolean handlePickupItemAction(Player player, ItemStack pickupItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Equipment equipment = equipmentRegistry.getUnassignedEquipment(pickupItem).orElse(null);

        if (equipment == null) {
            return true;
        }

        equipmentRegistry.assign(equipment, gamePlayer);
        equipment.onPickUp(gamePlayer);
        return true;
    }

    @Override
    public boolean handleRightClickAction(Player player, ItemStack clickedItem) {
        Equipment equipment = this.getAssignedEquipment(player, clickedItem);

        if (equipment == null) {
            return true;
        }

        equipment.onRightClick();
        return false;
    }

    @Override
    public boolean handleSwapFromAction(Player player, ItemStack swappedItem) {
        return true;
    }

    @Override
    public boolean handleSwapToAction(Player player, ItemStack swappedItem) {
        return true;
    }

    @Nullable
    private Equipment getAssignedEquipment(Player player, ItemStack itemStack) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return null;
        }

        Equipment equipment = equipmentRegistry.getAssignedEquipment(gamePlayer, itemStack).orElse(null);

        if (equipment == null || equipment.getHolder() != gamePlayer) {
            return null;
        }

        return equipment;
    }
}
