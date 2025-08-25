package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EquipmentActionExecutor implements ActionExecutor {

    @NotNull
    private ItemContainer<Equipment, EquipmentHolder> equipmentContainer;

    public EquipmentActionExecutor(@NotNull ItemContainer<Equipment, EquipmentHolder> equipmentContainer) {
        this.equipmentContainer = equipmentContainer;
    }

    public boolean handleChangeFromAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack changedItem) {
        return true;
    }

    public boolean handleChangeToAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack changedItem) {
        return true;
    }

    public boolean handleDropItemAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack droppedItem) {
        return true;
    }

    public boolean handleLeftClickAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        Equipment equipment = equipmentContainer.getAssignedItem(gamePlayer, clickedItem);

        if (equipment == null || equipment.getHolder() != gamePlayer) {
            return true;
        }

        equipment.onLeftClick();
        return false;
    }

    public boolean handleLeftClickAction(@NotNull Player player, @NotNull ItemStack clickedItem) {
        return true;
    }

    public boolean handlePickupItemAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack pickupItem) {
        return true;
    }

    public boolean handleRightClickAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        Equipment equipment = equipmentContainer.getAssignedItem(gamePlayer, clickedItem);

        if (equipment == null || equipment.getHolder() != gamePlayer) {
            return true;
        }

        equipment.onRightClick();
        return false;
    }

    public boolean handleRightClickAction(@NotNull Player player, @NotNull ItemStack clickedItem) {
        return true;
    }

    public boolean handleSwapFromAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack swappedItem) {
        return true;
    }

    public boolean handleSwapToAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack swappedItem) {
        return true;
    }
}
