package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class BaseItem implements Item {

    private final UUID id;
    @Nullable
    protected ItemStack itemStack;
    @Nullable
    protected String description;
    @Nullable
    protected String name;

    public BaseItem() {
        this.id = UUID.randomUUID();
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public UUID getId() {
        return id;
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
