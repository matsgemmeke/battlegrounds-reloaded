package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.PickupDispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemCreator;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemNotFoundException;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionResult;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class MeleeWeaponInteractionHandler implements ItemInteractionHandler {

    private static final String WEAPON_NAME_KEY = "weapon-name";

    private final ItemControllerRegistry itemControllerRegistry;
    private final ItemCreator itemCreator;
    private final Logger logger;
    private final MeleeWeaponRegistry meleeWeaponRegistry;
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public MeleeWeaponInteractionHandler(
            ItemControllerRegistry itemControllerRegistry,
            ItemCreator itemCreator,
            Logger logger,
            MeleeWeaponRegistry meleeWeaponRegistry,
            NamespacedKeyCreator namespacedKeyCreator
    ) {
        this.itemControllerRegistry = itemControllerRegistry;
        this.itemCreator = itemCreator;
        this.logger = logger;
        this.meleeWeaponRegistry = meleeWeaponRegistry;
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    @Override
    public DispatchResult handleChangeFrom(GamePlayer gamePlayer, ItemStack itemStack) {
        BiConsumer<MeleeWeapon, ItemController<MeleeWeaponUser>> consumer = (meleeWeapon, controller) -> controller.cancelAllFunctions();

        return this.handleInteraction(gamePlayer, itemStack, Action.CHANGE_FROM, consumer);
    }

    @Override
    public DispatchResult handleChangeTo(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.CHANGE_TO);
    }

    @Override
    public DispatchResult handleDropItem(GamePlayer gamePlayer, ItemStack itemStack) {
        BiConsumer<MeleeWeapon, ItemController<MeleeWeaponUser>> consumer = (meleeWeapon, controller) -> {
            controller.cancelAllFunctions();
            meleeWeapon.unassign();
        };

        return this.handleInteraction(gamePlayer, itemStack, Action.DROP_ITEM, consumer);
    }

    @Override
    public DispatchResult handleLeftClick(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.LEFT_CLICK);
    }

    @Override
    public PickupDispatchResult handlePickupItem(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handlePickupInteraction(gamePlayer, itemStack);
    }

    @Override
    public DispatchResult handleRightClick(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.RIGHT_CLICK);
    }

    @Override
    public DispatchResult handleSwapFrom(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.SWAP_FROM);
    }

    @Override
    public DispatchResult handleSwapTo(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.handleInteraction(gamePlayer, itemStack, Action.SWAP_TO);
    }

    private DispatchResult handleInteraction(GamePlayer gamePlayer, ItemStack itemStack, Action action) {
        return this.handleInteraction(gamePlayer, itemStack, action, (meleeWeapon, controller) -> {});
    }

    private DispatchResult handleInteraction(GamePlayer gamePlayer, ItemStack itemStack, Action action, BiConsumer<MeleeWeapon, ItemController<MeleeWeaponUser>> consumer) {
        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack).orElse(null);

        if (meleeWeapon == null) {
            return DispatchResult.unhandled();
        }

        ItemController<MeleeWeaponUser> controller = itemControllerRegistry.getMeleeWeaponController(meleeWeapon.getId()).orElse(null);

        if (controller == null) {
            return DispatchResult.unhandled();
        }

        ActionResult actionResult = controller.performAction(action, gamePlayer);

        consumer.accept(meleeWeapon, controller);

        return new DispatchResult(actionResult.performed(), actionResult.cancelEvent());
    }

    private PickupDispatchResult handlePickupInteraction(GamePlayer gamePlayer, ItemStack itemStack) {
        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack).orElse(null);

        // Check if the picked up item is a complete melee weapon, or a single projectile
        if (meleeWeapon != null) {
            return this.handlePickupItemCompleteMeleeWeapon(gamePlayer, meleeWeapon);
        } else {
            return this.handlePickupItemSingleProjectile(gamePlayer, itemStack);
        }
    }

    private PickupDispatchResult handlePickupItemCompleteMeleeWeapon(GamePlayer gamePlayer, MeleeWeapon meleeWeapon) {
        MeleeWeapon existingMeleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer).stream()
                .filter(m -> m.getName().equals(meleeWeapon.getName()))
                .findFirst()
                .orElse(null);

        if (existingMeleeWeapon != null) {
            return this.resupplyExistingMeleeWeapon(gamePlayer, meleeWeapon, existingMeleeWeapon);
        } else {
            return this.assignMeleeWeapon(gamePlayer, meleeWeapon);
        }
    }

    private PickupDispatchResult resupplyExistingMeleeWeapon(GamePlayer gamePlayer, MeleeWeapon pickedUpMeleeWeapon, MeleeWeapon existingMeleeWeapon) {
        Integer slot = gamePlayer.getItemSlot(existingMeleeWeapon).orElse(null);

        if (slot == null) {
            // We have already found an assigned melee weapon to the player's name, so we don't expect this scenario
            // to happen. If it somehow does, just treat the existing melee weapon as picked up, and do perform any
            // logic.
            return PickupDispatchResult.unhandled();
        }

        int pickedUpResourceAmount = pickedUpMeleeWeapon.getResourceContainer().getLoadedAmount() + pickedUpMeleeWeapon.getResourceContainer().getReserveAmount();
        int existingWeaponReserveAmount = existingMeleeWeapon.getResourceContainer().getReserveAmount();
        int updatedReserveAmount = Math.min(existingWeaponReserveAmount + pickedUpResourceAmount, existingMeleeWeapon.getResourceContainer().getMaxReserveAmount());

        existingMeleeWeapon.getResourceContainer().setReserveAmount(updatedReserveAmount);
        existingMeleeWeapon.update();

        gamePlayer.setItem(slot, existingMeleeWeapon.getItemStack());

        return PickupDispatchResult.cancelPickup();
    }

    private PickupDispatchResult assignMeleeWeapon(GamePlayer gamePlayer, MeleeWeapon meleeWeapon) {
        ItemController<MeleeWeaponUser> controller = itemControllerRegistry.getMeleeWeaponController(meleeWeapon.getId()).orElse(null);

        if (controller == null) {
            return PickupDispatchResult.unhandled();
        }

        meleeWeapon.assign(gamePlayer);

        controller.performAction(Action.PICKUP_ITEM, gamePlayer);

        return PickupDispatchResult.handled();
    }

    private PickupDispatchResult handlePickupItemSingleProjectile(GamePlayer gamePlayer, ItemStack itemStack) {
        NamespacedKey weaponNameKey = namespacedKeyCreator.create(WEAPON_NAME_KEY);
        String weaponName = itemStack.getItemMeta().getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING);

        MeleeWeapon existingMeleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer).stream()
                .filter(m -> m.getName().equals(weaponName))
                .filter(m -> m.getResourceContainer().getReserveAmount() < m.getResourceContainer().getMaxReserveAmount())
                .findFirst()
                .orElse(null);

        if (existingMeleeWeapon != null) {
            return this.addSingleResourceExistingMeleeWeapon(gamePlayer, existingMeleeWeapon);
        } else {
            return this.createAndAssignNewMeleeWeapon(gamePlayer, weaponName);
        }
    }

    private PickupDispatchResult addSingleResourceExistingMeleeWeapon(GamePlayer gamePlayer, MeleeWeapon existingMeleeWeapon) {
        Integer slot = gamePlayer.getItemSlot(existingMeleeWeapon).orElse(null);

        if (slot == null) {
            // We have already found an assigned melee weapon to the player's name, so we don't expect this scenario
            // to happen. If it somehow does, just treat the existing melee weapon as picked up, and do perform any
            // logic.
            return PickupDispatchResult.handled();
        }

        ResourceContainer resourceContainer = existingMeleeWeapon.getResourceContainer();
        resourceContainer.setReserveAmount(resourceContainer.getReserveAmount() + 1);

        existingMeleeWeapon.update();

        gamePlayer.setItem(slot, existingMeleeWeapon.getItemStack());

        return PickupDispatchResult.cancelPickup();
    }

    private PickupDispatchResult createAndAssignNewMeleeWeapon(GamePlayer gamePlayer, String weaponName) {
        MeleeWeapon meleeWeapon;

        try {
            meleeWeapon = itemCreator.createMeleeWeapon(weaponName, gamePlayer);
        } catch (ItemNotFoundException ex) {
            logger.warning("Player " + gamePlayer.getName() + " picked up item with unrecognized melee weapon name '" + weaponName + "' - ignoring pickup");
            return PickupDispatchResult.unhandled();
        }

        ItemController<MeleeWeaponUser> controller = itemControllerRegistry.getMeleeWeaponController(meleeWeapon.getId()).orElse(null);

        if (controller == null) {
            // Should not be possible, but return handled just in case
            return PickupDispatchResult.handled();
        }

        ResourceContainer resourceContainer = meleeWeapon.getResourceContainer();
        resourceContainer.setLoadedAmount(1);
        resourceContainer.setReserveAmount(0);

        controller.performAction(Action.PICKUP_ITEM, gamePlayer);

        meleeWeapon.update();

        gamePlayer.addItem(meleeWeapon.getItemStack());

        return PickupDispatchResult.cancelPickup();
    }
}
