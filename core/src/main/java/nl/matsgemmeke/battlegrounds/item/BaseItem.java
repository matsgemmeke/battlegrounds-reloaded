package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseItem implements Item {

    @NotNull
    protected GameContext context;
    @Nullable
    protected ItemStack itemStack;
    @Nullable
    protected String description;
    @Nullable
    protected String name;

    public BaseItem(@NotNull GameContext context) {
        this.context = context;
    }

    @NotNull
    public GameContext getContext() {
        return context;
    }

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

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return this.itemStack != null && this.itemStack.isSimilar(itemStack);
    }
}
