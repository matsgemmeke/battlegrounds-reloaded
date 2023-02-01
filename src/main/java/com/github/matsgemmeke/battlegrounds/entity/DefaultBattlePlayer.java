package com.github.matsgemmeke.battlegrounds.entity;

import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.item.BattleItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class DefaultBattlePlayer implements BattlePlayer {

    @NotNull
    private Player player;
    @NotNull
    private Set<BattleItem> items;

    public DefaultBattlePlayer(@NotNull Player player) {
        this.player = player;
        this.items = new HashSet<>();
    }

    @NotNull
    public Player getEntity() {
        return player;
    }

    @NotNull
    public Set<BattleItem> getItems() {
        return items;
    }

    public double damage(double damageAmount) {
        return 0.0;
    }

    @Nullable
    public BattleItem getBattleItem(@NotNull ItemStack itemStack) {
        for (BattleItem item : items) {
            if (item.getItemStack() != null && item.getItemStack().isSimilar(itemStack)) {
                return item;
            }
        }
        return null;
    }

    public double getRelativeAccuracy() {
        if (player.isSneaking()) {
            return 2.0;
        }
        if (player.isSprinting()) {
            return 0.5;
        }
        return 1.0;
    }
}
