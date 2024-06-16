package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EquipmentBehavior implements ItemBehavior {

    @NotNull
    private ItemRegister<Equipment, EquipmentHolder> register;

    public EquipmentBehavior(@NotNull ItemRegister<Equipment, EquipmentHolder> register) {
        this.register = register;
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
        Equipment equipment = register.getAssignedItem(gamePlayer, clickedItem);

        if (equipment == null || equipment.getHolder() != gamePlayer) {
            return true;
        }

        equipment.onLeftClick();
        return false;
    }

    public boolean handlePickupItemAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack pickupItem) {
        return true;
    }

    public boolean handleRightClickAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        Equipment equipment = register.getAssignedItem(gamePlayer, clickedItem);

        if (equipment == null || equipment.getHolder() != gamePlayer) {
            return true;
        }

        equipment.onRightClick();
        return false;
    }

    public boolean handleSwapFromAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack swappedItem) {
        return true;
    }

    public boolean handleSwapToAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack swappedItem) {
        return true;
    }
}
