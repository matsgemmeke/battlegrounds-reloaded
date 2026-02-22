package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GunActionExecutor implements ActionExecutor {

    private final GunRegistry gunRegistry;
    private final PlayerRegistry playerRegistry;

    @Inject
    public GunActionExecutor(GunRegistry gunRegistry, PlayerRegistry playerRegistry) {
        this.gunRegistry = gunRegistry;
        this.playerRegistry = playerRegistry;
    }

    @Override
    public boolean handleChangeFromAction(Player player, ItemStack changedItem) {
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

    @Override
    public boolean handleChangeToAction(Player player, ItemStack changedItem) {
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

    @Override
    public boolean handleDropItemAction(Player player, ItemStack droppedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        Gun gun = gunRegistry.getAssignedGun(gamePlayer, droppedItem).orElse(null);

        if (gun == null || gun.getHolder() != gamePlayer) {
            return true;
        }

        gunRegistry.unassign(gun);

        gun.onDrop();
        return true;
    }

    @Override
    public boolean handleLeftClickAction(Player player, ItemStack clickedItem) {
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

    @Override
    public boolean handlePickupItemAction(Player player, ItemStack pickupItem) {
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

    @Override
    public boolean handleRightClickAction(Player player, ItemStack clickedItem) {
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

    @Override
    public boolean handleSwapFromAction(Player player, ItemStack swappedItem) {
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

    @Override
    public boolean handleSwapToAction(Player player, ItemStack swappedItem) {
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
