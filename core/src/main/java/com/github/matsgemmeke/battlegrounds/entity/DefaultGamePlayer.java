package com.github.matsgemmeke.battlegrounds.entity;

import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Team;
import com.github.matsgemmeke.battlegrounds.api.item.Item;
import com.github.matsgemmeke.battlegrounds.api.item.PlayerEffect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class DefaultGamePlayer implements GamePlayer {

    private static final int OPERATING_FOOD_LEVEL = 6;

    private int priorFoodLevel;
    @NotNull
    private Player player;
    @NotNull
    private Set<Item> items;
    @NotNull
    private Set<PlayerEffect> effects;
    @Nullable
    private Team team;

    public DefaultGamePlayer(@NotNull Player player) {
        this.player = player;
        this.effects = new HashSet<>();
        this.items = new HashSet<>();
    }

    @NotNull
    public Player getEntity() {
        return player;
    }

    @Nullable
    public Team getTeam() {
        return team;
    }

    public void setTeam(@Nullable Team team) {
        this.team = team;
    }

    public boolean addEffect(@NotNull PlayerEffect effect) {
        return effects.add(effect);
    }

    public boolean removeEffect(@NotNull PlayerEffect effect) {
        return effects.remove(effect);
    }

    public boolean addItem(@NotNull Item item) {
        return items.add(item);
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
    public Item getItem(@NotNull ItemStack itemStack) {
        for (Item item : items) {
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

    public boolean removeItem(@NotNull Item item) {
        return items.remove(item);
    }

    public boolean updateItemStack(@NotNull ItemStack itemStack) {
        player.getInventory().setItemInMainHand(itemStack);
        return true;
    }
}
