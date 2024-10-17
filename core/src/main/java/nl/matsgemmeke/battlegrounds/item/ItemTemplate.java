package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

public class ItemTemplate {

    private int damage;
    @NotNull
    private Material material;
    @Nullable
    private TextTemplate displayNameTemplate;

    public ItemTemplate(@NotNull Material material) {
        this.material = material;
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

        if (damage > 0 && itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(damage);
        }

        if (displayNameTemplate != null) {
            String displayName = ChatColor.translateAlternateColorCodes('&', displayNameTemplate.replace(values));
            itemMeta.setDisplayName(displayName);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
