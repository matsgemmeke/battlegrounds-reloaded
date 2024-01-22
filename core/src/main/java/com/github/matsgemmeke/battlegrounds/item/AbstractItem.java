package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.item.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractItem implements Item {

    @NotNull
    protected GameContext context;
    @Nullable
    protected ItemHolder holder;
    @Nullable
    protected ItemStack itemStack;
    @Nullable
    protected String description;
    @Nullable
    protected String name;

    public AbstractItem(@NotNull GameContext context) {
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
    public ItemHolder getHolder() {
        return holder;
    }

    public void setHolder(@Nullable ItemHolder holder) {
        this.holder = holder;
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
