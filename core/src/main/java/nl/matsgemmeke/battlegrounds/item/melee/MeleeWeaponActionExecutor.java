package nl.matsgemmeke.battlegrounds.item.melee;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MeleeWeaponActionExecutor implements ActionExecutor {

    private final MeleeWeaponRegistry meleeWeaponRegistry;
    private final PlayerRegistry playerRegistry;

    @Inject
    public MeleeWeaponActionExecutor(MeleeWeaponRegistry meleeWeaponRegistry, PlayerRegistry playerRegistry) {
        this.meleeWeaponRegistry = meleeWeaponRegistry;
        this.playerRegistry = playerRegistry;
    }

    @Override
    public boolean handleChangeFromAction(Player player, ItemStack changedItem) {
        MeleeWeapon meleeWeapon = this.getMeleeWeapon(player, changedItem);

        if (meleeWeapon == null) {
            return true;
        }

        meleeWeapon.onChangeFrom();
        return true;
    }

    @Override
    public boolean handleChangeToAction(Player player, ItemStack changedItem) {
        MeleeWeapon meleeWeapon = this.getMeleeWeapon(player, changedItem);

        if (meleeWeapon == null) {
            return true;
        }

        meleeWeapon.onChangeTo();
        return true;
    }

    @Override
    public boolean handleDropItemAction(Player player, ItemStack droppedItem) {
        MeleeWeapon meleeWeapon = this.getMeleeWeapon(player, droppedItem);

        if (meleeWeapon == null) {
            return true;
        }

        meleeWeapon.onDrop();
        meleeWeaponRegistry.unassign(meleeWeapon);
        return true;
    }

    @Override
    public boolean handleLeftClickAction(Player player, ItemStack clickedItem) {
        MeleeWeapon meleeWeapon = this.getMeleeWeapon(player, clickedItem);

        if (meleeWeapon == null) {
            return true;
        }

        meleeWeapon.onLeftClick();
        return false;
    }

    @Override
    public boolean handlePickupItemAction(Player player, ItemStack pickupItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return true;
        }

        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, pickupItem).orElse(null);

        if (meleeWeapon == null || meleeWeapon.getHolder().orElse(null) != gamePlayer) {
            return true;
        }

        meleeWeapon.onPickUp(gamePlayer);
        meleeWeaponRegistry.assign(meleeWeapon, gamePlayer);
        return true;
    }

    @Override
    public boolean handleRightClickAction(Player player, ItemStack clickedItem) {
        MeleeWeapon meleeWeapon = this.getMeleeWeapon(player, clickedItem);

        if (meleeWeapon == null) {
            return true;
        }

        meleeWeapon.onRightClick();
        return false;
    }

    @Override
    public boolean handleSwapFromAction(Player player, ItemStack swappedItem) {
        MeleeWeapon meleeWeapon = this.getMeleeWeapon(player, swappedItem);

        if (meleeWeapon == null) {
            return true;
        }

        meleeWeapon.onSwapFrom();
        return false;
    }

    @Override
    public boolean handleSwapToAction(Player player, ItemStack swappedItem) {
        MeleeWeapon meleeWeapon = this.getMeleeWeapon(player, swappedItem);

        if (meleeWeapon == null) {
            return true;
        }

        meleeWeapon.onSwapTo();
        return false;
    }

    @Nullable
    private MeleeWeapon getMeleeWeapon(Player player, ItemStack itemStack) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return null;
        }

        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack).orElse(null);

        if (meleeWeapon == null || meleeWeapon.getHolder().orElse(null) != gamePlayer) {
            return null;
        }

        return meleeWeapon;
    }
}
