package nl.matsgemmeke.battlegrounds.item.equipment;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        return true;
    }

    @Override
    public boolean handleChangeToAction(Player player, ItemStack changedItem) {
        return true;
    }

    @Override
    public boolean handleDropItemAction(Player player, ItemStack droppedItem) {
        return true;
    }

    @Override
    public boolean handleLeftClickAction(Player player, ItemStack clickedItem) {
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

    @Override
    public boolean handlePickupItemAction(Player player, ItemStack pickupItem) {
        return true;
    }

    @Override
    public boolean handleRightClickAction(Player player, ItemStack clickedItem) {
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

    @Override
    public boolean handleSwapFromAction(Player player, ItemStack swappedItem) {
        return true;
    }

    @Override
    public boolean handleSwapToAction(Player player, ItemStack swappedItem) {
        return true;
    }
}
