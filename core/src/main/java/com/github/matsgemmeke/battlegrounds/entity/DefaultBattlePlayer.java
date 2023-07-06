package com.github.matsgemmeke.battlegrounds.entity;

import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Team;
import com.github.matsgemmeke.battlegrounds.api.item.BattleItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class DefaultBattlePlayer implements BattlePlayer {

    private static final int OPERATING_FOOD_LEVEL = 6;

    private int priorFoodLevel;
    @NotNull
    private Player player;
    @NotNull
    private Set<BattleItem> items;
    @Nullable
    private Team team;

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

    @Nullable
    public Team getTeam() {
        return team;
    }

    public void setTeam(@Nullable Team team) {
        this.team = team;
    }

    public double damage(double damageAmount) {
        return 0.0;
    }

    public void applyOperatingState(boolean operating) {
        if (operating) {
            priorFoodLevel = player.getFoodLevel();
            player.setFoodLevel(OPERATING_FOOD_LEVEL);
        } else {
            player.setFoodLevel(priorFoodLevel);
        }
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

    public boolean updateItemStack(@NotNull ItemStack itemStack) {
        player.getInventory().setItemInMainHand(itemStack);
        return true;
    }
}
