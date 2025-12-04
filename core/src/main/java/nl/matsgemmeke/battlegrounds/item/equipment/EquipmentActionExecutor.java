package nl.matsgemmeke.battlegrounds.item.equipment;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EquipmentActionExecutor implements ActionExecutor {

    @NotNull
    private final EquipmentRegistry equipmentRegistry;
    @NotNull
    private final PlayerRegistry playerRegistry;

    @Inject
    public EquipmentActionExecutor(@NotNull EquipmentRegistry equipmentRegistry, @NotNull PlayerRegistry playerRegistry) {
        this.equipmentRegistry = equipmentRegistry;
        this.playerRegistry = playerRegistry;
    }

    public boolean handleChangeFromAction(@NotNull Player player, @NotNull ItemStack changedItem) {
        return true;
    }

    public boolean handleChangeToAction(@NotNull Player player, @NotNull ItemStack changedItem) {
        return true;
    }

    public boolean handleDropItemAction(@NotNull Player player, @NotNull ItemStack droppedItem) {
        return true;
    }

    public boolean handleLeftClickAction(@NotNull Player player, @NotNull ItemStack clickedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Equipment equipment = equipmentRegistry.getAssignedEquipment(gamePlayer, clickedItem).orElse(null);

        if (equipment == null || equipment.getHolder() != gamePlayer) {
            return true;
        }

        equipment.onLeftClick();
        return false;
    }

    public boolean handlePickupItemAction(@NotNull Player player, @NotNull ItemStack pickupItem) {
        return true;
    }

    public boolean handleRightClickAction(@NotNull Player player, @NotNull ItemStack clickedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Equipment equipment = equipmentRegistry.getAssignedEquipment(gamePlayer, clickedItem).orElse(null);

        if (equipment == null || equipment.getHolder() != gamePlayer) {
            return true;
        }

        equipment.onRightClick();
        return false;
    }

    public boolean handleSwapFromAction(@NotNull Player player, @NotNull ItemStack swappedItem) {
        return true;
    }

    public boolean handleSwapToAction(@NotNull Player player, @NotNull ItemStack swappedItem) {
        return true;
    }
}
