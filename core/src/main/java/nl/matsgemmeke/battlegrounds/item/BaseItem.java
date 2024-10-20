package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class BaseItem implements Item {

    @Nullable
    protected ItemStack itemStack;
    @Nullable
    protected String description;
    @Nullable
    protected String name;

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }
}
