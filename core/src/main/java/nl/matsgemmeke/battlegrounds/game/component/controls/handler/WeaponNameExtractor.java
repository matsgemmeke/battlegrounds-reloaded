package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class WeaponNameExtractor {

    private static final String WEAPON_NAME_KEY = "weapon-name";

    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public WeaponNameExtractor(NamespacedKeyCreator namespacedKeyCreator) {
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    public Optional<String> extractWeaponName(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return Optional.empty();
        }

        NamespacedKey weaponNameKey = namespacedKeyCreator.create(WEAPON_NAME_KEY);
        String weaponName = itemMeta.getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING);

        return Optional.ofNullable(weaponName);
    }
}
