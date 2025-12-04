package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GunActionExecutor implements ActionExecutor {

    @NotNull
    private final GunRegistry gunRegistry;
    @NotNull
    private final PlayerRegistry playerRegistry;

    @Inject
    public GunActionExecutor(@NotNull GunRegistry gunRegistry, @NotNull PlayerRegistry playerRegistry) {
        this.gunRegistry = gunRegistry;
        this.playerRegistry = playerRegistry;
    }

    public boolean handleChangeFromAction(@NotNull Player player, @NotNull ItemStack changedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Gun gun = gunRegistry.getAssignedGun(gamePlayer, changedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onChangeFrom();
        return true;
    }

    public boolean handleChangeToAction(@NotNull Player player, @NotNull ItemStack changedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Gun gun = gunRegistry.getAssignedGun(gamePlayer, changedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onChangeTo();
        return true;
    }

    public boolean handleDropItemAction(@NotNull Player player, @NotNull ItemStack droppedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Gun gun = gunRegistry.getAssignedGun(gamePlayer, droppedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onDrop();

        gunRegistry.unassign(gun);
        return true;
    }

    public boolean handleLeftClickAction(@NotNull Player player, @NotNull ItemStack clickedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Gun gun = gunRegistry.getAssignedGun(gamePlayer, clickedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onLeftClick();
        return false;
    }

    public boolean handlePickupItemAction(@NotNull Player player, @NotNull ItemStack pickupItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Gun gun = gunRegistry.getUnassignedGun(pickupItem).orElse(null);

        if (gun == null) {
            return true;
        }

        gun.onPickUp(gamePlayer);

        gunRegistry.assign(gun, gamePlayer);
        return true;
    }

    public boolean handleRightClickAction(@NotNull Player player, @NotNull ItemStack clickedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Gun gun = gunRegistry.getAssignedGun(gamePlayer, clickedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onRightClick();
        return false;
    }

    public boolean handleSwapFromAction(@NotNull Player player, @NotNull ItemStack swappedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Gun gun = gunRegistry.getAssignedGun(gamePlayer, swappedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onSwapFrom();
        return false;
    }

    public boolean handleSwapToAction(@NotNull Player player, @NotNull ItemStack swappedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Gun gun = gunRegistry.getAssignedGun(gamePlayer, swappedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gun.onSwapTo();
        return true;
    }
}
