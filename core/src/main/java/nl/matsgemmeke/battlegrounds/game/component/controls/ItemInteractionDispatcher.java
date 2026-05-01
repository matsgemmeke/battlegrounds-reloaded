package nl.matsgemmeke.battlegrounds.game.component.controls;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.ItemInteractionHandler;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemInteractionDispatcher {

    private static final String WEAPON_TYPE_KEY = "weapon-type";

    private final Map<Action, Map<WeaponType, ItemInteractionHandler>> interactionHandlers;
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public ItemInteractionDispatcher(NamespacedKeyCreator namespacedKeyCreator) {
        this.namespacedKeyCreator = namespacedKeyCreator;
        this.interactionHandlers = new ConcurrentHashMap<>();
    }

    public void registerInteractionHandler(Action action, WeaponType weaponType, ItemInteractionHandler interactionHandler) {
        interactionHandlers.computeIfAbsent(action, k -> new HashMap<>()).put(weaponType, interactionHandler);
    }

    public DispatchResult dispatch(GamePlayer gamePlayer, ItemStack itemStack, Action action) {
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
        ItemInteractionHandler interactionHandler = interactionHandlers.getOrDefault(action, Collections.emptyMap()).get(weaponType);

        if (interactionHandler == null) {
            return DispatchResult.unhandled();
        }

        DispatchResult result = interactionHandler.handleInteraction(gamePlayer, itemStack, action);

        if (result.handled()) {
            return result;
        }

        return DispatchResult.unhandled();
    }
}
