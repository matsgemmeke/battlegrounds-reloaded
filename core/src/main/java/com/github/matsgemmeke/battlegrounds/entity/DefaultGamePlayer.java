package com.github.matsgemmeke.battlegrounds.entity;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Team;
import com.github.matsgemmeke.battlegrounds.api.item.Item;
import com.github.matsgemmeke.battlegrounds.api.item.PlayerEffect;
import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class DefaultGamePlayer implements GamePlayer {

    private static final float NORMAL_ACCURACY = 1.0f;
    private static final float SNEAKING_ACCURACY = 2.0f;
    private static final float SPRINTING_ACCURACY = 0.5f;
    private static final int OPERATING_FOOD_LEVEL = 6;

    private int priorFoodLevel;
    @NotNull
    private InternalsProvider internals;
    @NotNull
    private Player player;
    @NotNull
    private Set<Item> items;
    @NotNull
    private Set<PlayerEffect> effects;
    @NotNull
    private Set<Weapon> weapons;
    @Nullable
    private Team team;

    public DefaultGamePlayer(@NotNull Player player, @NotNull InternalsProvider internals) {
        this.player = player;
        this.internals = internals;
        this.effects = new HashSet<>();
        this.items = new HashSet<>();
        this.weapons = new HashSet<>();
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

    public boolean addWeapon(@NotNull Weapon weapon) {
        return weapons.add(weapon);
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

    public boolean canReceiveRecoil() {
        return player.isOnline() && !player.isDead();
    }

    @NotNull
    public Location getAimDirection() {
        return player.getEyeLocation().subtract(0, 0.25, 0);
    }

    @Nullable
    public Item getItem(@NotNull ItemStack itemStack) {
        for (Item item : items) {
            if (item.isMatching(itemStack)) {
                return item;
            }
        }
        return null;
    }

    @Nullable
    public Weapon getWeapon(@NotNull ItemStack itemStack) {
        for (Weapon weapon : weapons) {
            if (weapon.isMatching(itemStack)) {
                return weapon;
            }
        }
        return null;
    }

    public float getRelativeAccuracy() {
        if (player.isSneaking()) {
            return SNEAKING_ACCURACY;
        }

        if (player.isSprinting()) {
            return SPRINTING_ACCURACY;
        }

        return NORMAL_ACCURACY;
    }

    public void modifyCameraRotation(float yaw, float pitch) {
        internals.setPlayerRotation(player, yaw, pitch);
    }

    public boolean removeItem(@NotNull Item item) {
        return items.remove(item);
    }

    public boolean removeWeapon(@NotNull Weapon weapon) {
        return weapons.remove(weapon);
    }

    public boolean updateItemStack(@NotNull ItemStack itemStack) {
        player.getInventory().setItemInMainHand(itemStack);
        return true;
    }
}
