package nl.matsgemmeke.battlegrounds.game.component.controls;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.ItemInteractionHandler;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.DispatchResult;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Function;

public class ItemInteractionDispatcher {

    private static final String WEAPON_TYPE_KEY = "weapon-type";

    private final Map<WeaponType, ItemInteractionHandler> interactionHandlers;
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public ItemInteractionDispatcher(NamespacedKeyCreator namespacedKeyCreator) {
        this.namespacedKeyCreator = namespacedKeyCreator;
        this.interactionHandlers = new HashMap<>();
    }

    public void registerInteractionHandler(WeaponType weaponType, ItemInteractionHandler interactionHandler) {
        interactionHandlers.put(weaponType, interactionHandler);
    }

    public DispatchResult dispatchChangeFrom(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.dispatchInteraction(itemStack, itemInteractionHandler -> itemInteractionHandler.handleChangeFrom(gamePlayer, itemStack));
    }

    public DispatchResult dispatchChangeTo(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.dispatchInteraction(itemStack, itemInteractionHandler -> itemInteractionHandler.handleChangeTo(gamePlayer, itemStack));
    }

    public DispatchResult dispatchDropItem(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.dispatchInteraction(itemStack, itemInteractionHandler -> itemInteractionHandler.handleDropItem(gamePlayer, itemStack));
    }

    public DispatchResult dispatchLeftClick(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.dispatchInteraction(itemStack, itemInteractionHandler -> itemInteractionHandler.handleLeftClick(gamePlayer, itemStack));
    }

    public DispatchResult dispatchPickupItem(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.dispatchInteraction(itemStack, itemInteractionHandler -> itemInteractionHandler.handlePickupItem(gamePlayer, itemStack));
    }

    public DispatchResult dispatchRightClick(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.dispatchInteraction(itemStack, itemInteractionHandler -> itemInteractionHandler.handleRightClick(gamePlayer, itemStack));
    }

    public DispatchResult dispatchSwapFrom(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.dispatchInteraction(itemStack, itemInteractionHandler -> itemInteractionHandler.handleSwapFrom(gamePlayer, itemStack));
    }

    public DispatchResult dispatchSwapTo(GamePlayer gamePlayer, ItemStack itemStack) {
        return this.dispatchInteraction(itemStack, itemInteractionHandler -> itemInteractionHandler.handleSwapTo(gamePlayer, itemStack));
    }

    private DispatchResult dispatchInteraction(ItemStack itemStack, Function<ItemInteractionHandler, DispatchResult> interactionFunction) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return DispatchResult.unhandled();
        }

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        NamespacedKey key = namespacedKeyCreator.create(WEAPON_TYPE_KEY);
        String weaponTypeValue = data.get(key, PersistentDataType.STRING);

        if (weaponTypeValue == null) {
            return DispatchResult.unhandled();
        }

        WeaponType weaponType = WeaponType.valueOf(weaponTypeValue);
        ItemInteractionHandler interactionHandler = interactionHandlers.get(weaponType);

        if (interactionHandler == null) {
            return DispatchResult.unhandled();
        }

        DispatchResult result = interactionFunction.apply(interactionHandler);

        if (result.handled()) {
            return result;
        }

        return DispatchResult.unhandled();
    }
}
