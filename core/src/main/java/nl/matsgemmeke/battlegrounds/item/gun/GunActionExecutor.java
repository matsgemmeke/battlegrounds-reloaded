package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GunActionExecutor implements ActionExecutor {

    @NotNull
    private final GunRegistry gunRegistry;

    @Inject
    public GunActionExecutor(@NotNull GunRegistry gunRegistry) {
        this.gunRegistry = gunRegistry;
    }

    public boolean handleChangeFromAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack changedItem) {
        Gun gun = gunRegistry.getAssignedGun(gamePlayer, changedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onChangeFrom();
        return true;
    }

    public boolean handleChangeToAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack changedItem) {
        Gun gun = gunRegistry.getAssignedGun(gamePlayer, changedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onChangeTo();
        return true;
    }

    public boolean handleDropItemAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack droppedItem) {
        Gun gun = gunRegistry.getAssignedGun(gamePlayer, droppedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onDrop();

        gunRegistry.unassign(gun);
        return true;
    }

    public boolean handleLeftClickAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        Gun gun = gunRegistry.getAssignedGun(gamePlayer, clickedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onLeftClick();
        return false;
    }

    public boolean handleLeftClickAction(@NotNull Player player, @NotNull ItemStack clickedItem) {
        return true;
    }

    public boolean handlePickupItemAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack pickupItem) {
        Gun gun = gunRegistry.getUnassignedGun(pickupItem).orElse(null);

        if (gun == null) {
            return true;
        }

        gun.onPickUp(gamePlayer);

        gunRegistry.assign(gun, gamePlayer);
        return true;
    }

    public boolean handleRightClickAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        Gun gun = gunRegistry.getAssignedGun(gamePlayer, clickedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onRightClick();
        return false;
    }

    public boolean handleRightClickAction(@NotNull Player player, @NotNull ItemStack clickedItem) {
        return true;
    }

    public boolean handleSwapFromAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack swappedItem) {
        Gun gun = gunRegistry.getAssignedGun(gamePlayer, swappedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onSwapFrom();
        return false;
    }

    public boolean handleSwapToAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack swappedItem) {
        Gun gun = gunRegistry.getAssignedGun(gamePlayer, swappedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onSwapTo();
        return true;
    }
}
