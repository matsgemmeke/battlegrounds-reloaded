package nl.matsgemmeke.battlegrounds.item.melee;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.action.ActionExecutor;
import nl.matsgemmeke.battlegrounds.item.action.PickupActionResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MeleeWeaponActionExecutor implements ActionExecutor {

    private static final String WEAPON_NAME_KEY = "weapon-name";

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

        meleeWeaponRegistry.unassign(meleeWeapon);
        meleeWeapon.onDrop();
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
    public PickupActionResult handlePickupAction(Player player, ItemStack pickupItem) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(player.getUniqueId()).orElse(null);

        if (gamePlayer == null) {
            return new PickupActionResult(true, false);
        }

        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getUnassignedMeleeWeapon(pickupItem).orElse(null);

        // Check if the picked up item is a complete melee weapon, or a single projectile
        if (meleeWeapon != null) {
            return this.handlePickupItemCompleteMeleeWeapon(gamePlayer, meleeWeapon);
        } else {

        }

        return new PickupActionResult(true, false);
    }

    private PickupActionResult handlePickupItemCompleteMeleeWeapon(GamePlayer gamePlayer, MeleeWeapon meleeWeapon) {
        MeleeWeapon existingMeleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer).stream()
                .filter(m -> m.getName().equals(meleeWeapon.getName()))
                .findFirst()
                .orElse(null);

        if (existingMeleeWeapon != null) {
            int totalResourceAmount = meleeWeapon.getResourceContainer().getLoadedAmount() + meleeWeapon.getResourceContainer().getReserveAmount();
            int existingWeaponReserveAmount = existingMeleeWeapon.getResourceContainer().getReserveAmount();

            existingMeleeWeapon.getResourceContainer().setReserveAmount(existingWeaponReserveAmount + totalResourceAmount);
            existingMeleeWeapon.update();

            return new PickupActionResult(false, true);
        } else {

        }

        return new PickupActionResult(true, false);
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
