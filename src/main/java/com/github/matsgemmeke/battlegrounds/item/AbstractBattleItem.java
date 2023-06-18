package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.item.BattleItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBattleItem implements BattleItem {

    @NotNull
    protected BattleContext context;
    @Nullable
    protected BattleEntity holder;
    @Nullable
    protected ItemStack itemStack;
    @Nullable
    protected String description;
    @NotNull
    protected String id;
    @NotNull
    protected String name;

    public AbstractBattleItem(
            @NotNull String id,
            @NotNull String name,
            @NotNull BattleContext context
    ) {
        this.id = id;
        this.name = name;
        this.context = context;
    }

    @NotNull
    public BattleContext getContext() {
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
    public BattleEntity getHolder() {
        return holder;
    }

    public void setHolder(@Nullable BattleEntity holder) {
        this.holder = holder;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
