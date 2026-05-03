package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemCreator;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class MeleeWeaponPickupHandler implements ItemInteractionHandler {

    private static final String WEAPON_NAME_KEY = "weapon-name";

    private final ItemControllerRegistry itemControllerRegistry;
    private final ItemCreator itemCreator;
    private final MeleeWeaponRegistry meleeWeaponRegistry;
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public MeleeWeaponPickupHandler(
            ItemControllerRegistry itemControllerRegistry,
            ItemCreator itemCreator,
            MeleeWeaponRegistry meleeWeaponRegistry,
            NamespacedKeyCreator namespacedKeyCreator
    ) {
        this.itemControllerRegistry = itemControllerRegistry;
        this.itemCreator = itemCreator;
        this.meleeWeaponRegistry = meleeWeaponRegistry;
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    @Override
    public DispatchResult handleInteraction(GamePlayer gamePlayer, ItemStack itemStack, Action action) {
        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack).orElse(null);

        // Check if the picked up item is a complete melee weapon, or a single projectile
        if (meleeWeapon != null) {
            return this.handlePickupItemCompleteMeleeWeapon(gamePlayer, meleeWeapon);
        } else {
            return this.handlePickupItemSingleProjectile(gamePlayer, itemStack);
        }
    }

    private DispatchResult handlePickupItemCompleteMeleeWeapon(GamePlayer gamePlayer, MeleeWeapon meleeWeapon) {
        MeleeWeapon existingMeleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer).stream()
                .filter(m -> m.getName().equals(meleeWeapon.getName()))
                .findFirst()
                .orElse(null);

        if (existingMeleeWeapon != null) {
            System.out.println("resupply existing melee weapon");
            return this.resupplyExistingMeleeWeapon(gamePlayer, meleeWeapon, existingMeleeWeapon);
        } else {
            System.out.println("assign melee weapon");
            return this.assignMeleeWeapon(gamePlayer, meleeWeapon);
        }
    }

    private DispatchResult resupplyExistingMeleeWeapon(GamePlayer gamePlayer, MeleeWeapon pickedUpMeleeWeapon, MeleeWeapon existingMeleeWeapon) {
        Integer slot = gamePlayer.getItemSlot(existingMeleeWeapon).orElse(null);

        if (slot == null) {
            // We have already found an assigned melee weapon to the player's name, so we don't expect this scenario
            // to happen. If it somehow does, just treat the existing melee weapon as picked up, and do perform any
            // logic.
            return DispatchResult.unhandled();
        }

        int pickedUpResourceAmount = pickedUpMeleeWeapon.getResourceContainer().getLoadedAmount() + pickedUpMeleeWeapon.getResourceContainer().getReserveAmount();
        int existingWeaponReserveAmount = existingMeleeWeapon.getResourceContainer().getReserveAmount();
        int updatedReserveAmount = Math.min(existingWeaponReserveAmount + pickedUpResourceAmount, existingMeleeWeapon.getResourceContainer().getMaxReserveAmount());

        existingMeleeWeapon.getResourceContainer().setReserveAmount(updatedReserveAmount);
        existingMeleeWeapon.update();

        gamePlayer.setItem(slot, existingMeleeWeapon.getItemStack());

        return new DispatchResult(true, true);
    }

    private DispatchResult assignMeleeWeapon(GamePlayer gamePlayer, MeleeWeapon meleeWeapon) {
        ItemController<MeleeWeaponUser> controller = itemControllerRegistry.getMeleeWeaponController(meleeWeapon.getId()).orElse(null);

        if (controller == null) {
            return DispatchResult.unhandled();
        }

        meleeWeapon.assign(gamePlayer);
        meleeWeapon.onPickUp(gamePlayer);

        controller.performActionNew(Action.PICKUP_ITEM, gamePlayer);

        return new DispatchResult(true, true);
    }

    private DispatchResult handlePickupItemSingleProjectile(GamePlayer gamePlayer, ItemStack itemStack) {
        NamespacedKey weaponNameKey = namespacedKeyCreator.create(WEAPON_NAME_KEY);
        String weaponName = itemStack.getItemMeta().getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING);

        MeleeWeapon existingMeleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer).stream()
                .filter(m -> m.getName().equals(weaponName))
                .filter(m -> m.getResourceContainer().getReserveAmount() < m.getResourceContainer().getMaxReserveAmount())
                .findFirst()
                .orElse(null);

        if (existingMeleeWeapon != null) {
            System.out.println("add single resource to existing melee weapon");
            return this.addSingleResourceExistingMeleeWeapon(gamePlayer, existingMeleeWeapon);
        } else {
            System.out.println("create and assign new melee weapon");
            return this.createAndAssignNewMeleeWeapon(gamePlayer, weaponName);
        }
    }

    private DispatchResult addSingleResourceExistingMeleeWeapon(GamePlayer gamePlayer, MeleeWeapon existingMeleeWeapon) {
        Integer slot = gamePlayer.getItemSlot(existingMeleeWeapon).orElse(null);

        if (slot == null) {
            // We have already found an assigned melee weapon to the player's name, so we don't expect this scenario
            // to happen. If it somehow does, just treat the existing melee weapon as picked up, and do perform any
            // logic.
            return new DispatchResult(true, false);
        }

        ResourceContainer resourceContainer = existingMeleeWeapon.getResourceContainer();
        resourceContainer.setReserveAmount(resourceContainer.getReserveAmount() + 1);

        existingMeleeWeapon.update();

        gamePlayer.setItem(slot, existingMeleeWeapon.getItemStack());

        return new DispatchResult(true, true);
    }

    private DispatchResult createAndAssignNewMeleeWeapon(GamePlayer gamePlayer, String weaponName) {
        MeleeWeapon meleeWeapon = itemCreator.createMeleeWeapon(weaponName, gamePlayer);
        ItemController<MeleeWeaponUser> controller = itemControllerRegistry.getMeleeWeaponController(meleeWeapon.getId()).orElse(null);

        if (controller == null) {
            // Should not be possible, but return unhandled just in case
            return DispatchResult.unhandled();
        }

        ResourceContainer resourceContainer = meleeWeapon.getResourceContainer();
        resourceContainer.setLoadedAmount(1);
        resourceContainer.setReserveAmount(0);

        meleeWeapon.update();

        gamePlayer.addItem(meleeWeapon.getItemStack());

        return new DispatchResult(true, true);
    }
}
