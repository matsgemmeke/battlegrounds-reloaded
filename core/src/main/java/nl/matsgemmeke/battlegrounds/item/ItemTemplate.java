package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.UUIDDataType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class that defines a template for creating {@link ItemStack} instances for items.
 */
public class ItemTemplate {

    private final boolean unbreakable;
    private final int damage;
    private final List<ItemFlag> itemFlags;
    private final List<PersistentDataEntry<?, ?>> dataEntries;
    private final Material material;
    private final NamespacedKey templateKey;
    @Nullable
    private final TextTemplate displayNameTemplate;
    private final UUID templateId;

    private ItemTemplate(Builder builder) {
        this.templateKey = builder.templateKey;
        this.templateId = builder.templateId;
        this.material = builder.material;
        this.damage = builder.damage;
        this.itemFlags = builder.itemFlags;
        this.dataEntries = builder.dataEntries;
        this.displayNameTemplate = builder.displayNameTemplate;
        this.unbreakable = builder.unbreakable;
    }

    public static Builder builder(NamespacedKey key, UUID id, Material material) {
        return new Builder(key, id, material);
    }

    /**
     * Constructs a new {@link ItemStack} instance.
     *
     * @return the constructed item stack
     */
    public ItemStack createItemStack() {
        return this.createItemStack(Collections.emptyMap());
    }

    public void apply(ItemStack itemStack, Map<String, Object> values) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        // In case the item stack does not come with an item meta, only return the item stack as is
        if (itemMeta == null) {
            return;
        }

        itemMeta.setUnbreakable(unbreakable);

        itemFlags.forEach(itemMeta::addItemFlags);

        if (damage > 0 && itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(damage);
        }

        if (displayNameTemplate != null) {
            String displayName = ChatColor.translateAlternateColorCodes('&', displayNameTemplate.replace(values));
            itemMeta.setDisplayName(displayName);
        }

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(templateKey, new UUIDDataType(), templateId);

        dataEntries.forEach(dataEntry -> this.setDataEntry(persistentDataContainer, dataEntry));

        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Constructs a new {@link ItemStack} instance while using template values.
     *
     * @param placeholderValues the placeholder values
     * @return the constructed item stack
     */
    public ItemStack createItemStack(Map<String, Object> placeholderValues) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        // In case the item stack does not come with an item meta, only return the item stack as is
        if (itemMeta == null) {
            return itemStack;
        }

        itemMeta.setUnbreakable(unbreakable);

        itemFlags.forEach(itemMeta::addItemFlags);

        if (damage > 0 && itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(damage);
        }

        if (displayNameTemplate != null) {
            String displayName = ChatColor.translateAlternateColorCodes('&', displayNameTemplate.replace(placeholderValues));
            itemMeta.setDisplayName(displayName);
        }

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(templateKey, new UUIDDataType(), templateId);

        dataEntries.forEach(dataEntry -> this.setDataEntry(persistentDataContainer, dataEntry));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private <T, Z> void setDataEntry(PersistentDataContainer container, PersistentDataEntry<T, Z> entry) {
        container.set(entry.key(), entry.type(), entry.value());
    }

    /**
     * Checks whether a given {@link ItemStack} corresponds with the template. An item stack will only match with the
     * item template it was created by.
     *
     * @param itemStack the item stack
     * @return whether the item stack matches with the template
     */
    public boolean matchesTemplate(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return false;
        }

        UUID uuid = itemMeta.getPersistentDataContainer().get(templateKey, new UUIDDataType());
        return uuid != null && uuid.equals(templateId);
    }

    public static class Builder {

        private final NamespacedKey templateKey;
        private final UUID templateId;
        private final Material material;
        private final List<ItemFlag> itemFlags;
        private final List<PersistentDataEntry<?, ?>> dataEntries;
        private boolean unbreakable;
        private int damage;
        private TextTemplate displayNameTemplate;

        private Builder(NamespacedKey templateKey, UUID templateId, Material material) {
            this.templateKey = templateKey;
            this.templateId = templateId;
            this.material = material;
            this.itemFlags = new ArrayList<>();
            this.dataEntries = new ArrayList<>();
            this.damage = 0;
            this.unbreakable = false;
        }

        public Builder damage(int damage) {
            this.damage = damage;
            return this;
        }

        public Builder dataEntries(Collection<PersistentDataEntry<?, ?>> entries) {
            this.dataEntries.addAll(entries);
            return this;
        }

        public Builder displayNameTemplate(TextTemplate displayNameTemplate) {
            this.displayNameTemplate = displayNameTemplate;
            return this;
        }

        public Builder itemFlags(Collection<ItemFlag> itemFlags) {
            this.itemFlags.addAll(itemFlags);
            return this;
        }

        public Builder unbreakable(boolean value) {
            this.unbreakable = value;
            return this;
        }

        public ItemTemplate build() {
            return new ItemTemplate(this);
        }
    }
}
