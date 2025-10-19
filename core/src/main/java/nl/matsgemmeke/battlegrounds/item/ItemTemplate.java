package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.UUIDDataType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class that defines a template for creating {@link ItemStack} instances for items.
 */
public class ItemTemplate {

    private final List<PersistentDataEntry<?, ?>> dataEntries;
    private final Material material;
    private final NamespacedKey templateKey;
    private final UUID templateId;
    private int damage;
    @Nullable
    private TextTemplate displayNameTemplate;

    public ItemTemplate(NamespacedKey templateKey, UUID templateId, Material material) {
        this.templateKey = templateKey;
        this.templateId = templateId;
        this.material = material;
        this.dataEntries = new ArrayList<>();
    }

    /**
     * Adds a persistent data entry to the template.
     *
     * @param dataEntry the data entry
     */
    public void addPersistentDataEntry(PersistentDataEntry<?, ?> dataEntry) {
        dataEntries.add(dataEntry);
    }

    /**
     * Gets the template damage value used to apply damage to the constructed {@link ItemStack} instances.
     *
     * @return the template damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Sets the template damage value used to apply damage to the constructed {@link ItemStack} instances.
     *
     * @param damage the template damage
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Gets the text template used to create the display name of the constructed {@link ItemStack} instances.
     *
     * @return the template display name text template
     */
    public Optional<TextTemplate> getDisplayNameTemplate() {
        return Optional.ofNullable(displayNameTemplate);
    }

    /**
     * Sets the text template used to create the display name of the constructed {@link ItemStack} instances.
     *
     * @param displayNameTemplate the template display name text template
     */
    public void setDisplayNameTemplate(@Nullable TextTemplate displayNameTemplate) {
        this.displayNameTemplate = displayNameTemplate;
    }

    /**
     * Constructs a new {@link ItemStack} instance.
     *
     * @return the constructed item stack
     */
    @NotNull
    public ItemStack createItemStack() {
        return this.createItemStack(Collections.emptyMap());
    }

    /**
     * Constructs a new {@link ItemStack} instance while using template values.
     *
     * @param placeholderValues the placeholder values
     * @return the constructed item stack
     */
    @NotNull
    public ItemStack createItemStack(@NotNull Map<String, Object> placeholderValues) {
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
            String displayName = ChatColor.translateAlternateColorCodes('&', displayNameTemplate.replace(placeholderValues));
            itemMeta.setDisplayName(displayName);
        }

        itemMeta.getPersistentDataContainer().set(templateKey, new UUIDDataType(), templateId);

        for (PersistentDataEntry<?, ?> dataEntry : dataEntries) {
            this.applyDataEntry(itemMeta.getPersistentDataContainer(), dataEntry);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private <T, Z> void applyDataEntry(PersistentDataContainer container, PersistentDataEntry<T, Z> entry) {
        container.set(entry.key(), entry.type(), entry.value());
    }

    /**
     * Checks whether a given {@link ItemStack} corresponds with the template. An item stack will only match with the
     * item template it was created by.
     *
     * @param itemStack the item stack
     * @return whether the item stack matches with the template
     */
    public boolean matchesTemplate(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return false;
        }

        UUID uuid = itemMeta.getPersistentDataContainer().get(templateKey, new UUIDDataType());
        return uuid != null && uuid.equals(templateId);
    }
}
