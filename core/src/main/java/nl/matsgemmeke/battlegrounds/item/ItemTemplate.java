package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.UUIDDataType;
import nl.matsgemmeke.battlegrounds.util.UUIDGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ItemTemplate {

    private int damage;
    @NotNull
    private Material material;
    @NotNull
    private NamespacedKey key;
    @Nullable
    private TextTemplate displayNameTemplate;
    @NotNull
    private UUID uuid;

    public ItemTemplate(@NotNull NamespacedKey key, @NotNull Material material, @NotNull UUIDGenerator uuidGenerator) {
        this.key = key;
        this.material = material;
        this.uuid = uuidGenerator.generateRandom();
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Nullable
    public TextTemplate getDisplayNameTemplate() {
        return displayNameTemplate;
    }

    public void setDisplayNameTemplate(@Nullable TextTemplate displayNameTemplate) {
        this.displayNameTemplate = displayNameTemplate;
    }

    @NotNull
    public ItemStack createItemStack() {
        return this.createItemStack(Collections.emptyMap());
    }

    @NotNull
    public ItemStack createItemStack(@NotNull Map<String, Object> values) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        // In case the item stack does not come with an item meta, only return the item stack as is
        if (itemMeta == null) {
            return itemStack;
        }

        if (damage > 0 && itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(damage);
        }

        if (displayNameTemplate != null) {
            String displayName = ChatColor.translateAlternateColorCodes('&', displayNameTemplate.replace(values));
            itemMeta.setDisplayName(displayName);
        }

        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), uuid);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public boolean matchesTemplate(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return false;
        }

        UUID uuid = itemMeta.getPersistentDataContainer().get(key, new UUIDDataType());
        return uuid != null && uuid.equals(this.uuid);
    }
}
