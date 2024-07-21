package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GunBehavior implements ItemBehavior {

    @NotNull
    private ItemStorage<Gun, GunHolder> storage;

    public GunBehavior(@NotNull ItemStorage<Gun, GunHolder> storage) {
        this.storage = storage;
    }

    public boolean handleChangeFromAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack changedItem) {
        Gun gun = storage.getAssignedItem(gamePlayer, changedItem);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onChangeFrom();
        return true;
    }

    public boolean handleChangeToAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack changedItem) {
        Gun gun = storage.getAssignedItem(gamePlayer, changedItem);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onChangeTo();
        return true;
    }

    public boolean handleDropItemAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack droppedItem) {
        Gun gun = storage.getAssignedItem(gamePlayer, droppedItem);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onDrop();

        storage.removeAssignedItem(gun, gamePlayer);
        storage.addUnassignedItem(gun);
        return true;
    }

    public boolean handleLeftClickAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        Gun gun = storage.getAssignedItem(gamePlayer, clickedItem);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onLeftClick();
        return false;
    }

    public boolean handlePickupItemAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack pickupItem) {
        Gun gun = storage.getUnassignedItem(pickupItem);

        if (gun == null) {
            return true;
        }

        gun.onPickUp(gamePlayer);

        storage.removeUnassignedItem(gun);
        storage.addAssignedItem(gun, gamePlayer);
        return true;
    }

    public boolean handleRightClickAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        Gun gun = storage.getAssignedItem(gamePlayer, clickedItem);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onRightClick();
        return false;
    }

    public boolean handleSwapFromAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack swappedItem) {
        Gun gun = storage.getAssignedItem(gamePlayer, swappedItem);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onSwapFrom();
        return false;
    }

    public boolean handleSwapToAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack swappedItem) {
        Gun gun = storage.getAssignedItem(gamePlayer, swappedItem);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onSwapTo();
        return true;
    }
}
