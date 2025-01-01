package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultGamePlayer implements GamePlayer {

    private static final double DAMAGE_ANIMATION_DAMAGE = 0.001;
    private static final float NORMAL_ACCURACY = 1.0f;
    private static final float SNEAKING_ACCURACY = 2.0f;
    private static final float SPRINTING_ACCURACY = 0.5f;
    private static final int OPERATING_FOOD_LEVEL = 6;

    private boolean passive;
    private int previousFoodLevel;
    @NotNull
    private InternalsProvider internals;
    @NotNull
    private Player player;
    @NotNull
    private Set<ItemEffect> effects;

    public DefaultGamePlayer(@NotNull Player player, @NotNull InternalsProvider internals) {
        this.player = player;
        this.internals = internals;
        this.effects = new HashSet<>();
        this.passive = false;
    }

    @NotNull
    public Player getEntity() {
        return player;
    }

    public double getHealth() {
        return player.getHealth();
    }

    public void setHealth(double health) {
        player.setHealth(health);
    }

    @NotNull
    public Location getLocation() {
        return player.getLocation();
    }

    @NotNull
    public String getName() {
        return player.getName();
    }

    @NotNull
    public World getWorld() {
        return player.getWorld();
    }

    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    public boolean addEffect(@NotNull ItemEffect effect) {
        return effects.add(effect);
    }

    public boolean removeEffect(@NotNull ItemEffect effect) {
        return effects.remove(effect);
    }

    public void applyReloadingState() {
        previousFoodLevel = player.getFoodLevel();
        player.setFoodLevel(OPERATING_FOOD_LEVEL);
    }

    public void resetReloadingState() {
        player.setFoodLevel(previousFoodLevel);
    }

    public void applyViewMagnification(float magnification) {
        internals.setWalkSpeed(player, magnification);
    }

    public boolean canReceiveRecoil() {
        return player.isOnline() && !player.isDead();
    }

    public double damage(@NotNull Damage damage) {
        if (player.isDead() || player.getHealth() <= 0.0) {
            return 0.0;
        }

        // Divide by 5 to convert to hearts value
        double finalHealth = Math.max(player.getHealth() - damage.amount() / 5, 0.0);

        // Create fake damage animation
        player.damage(DAMAGE_ANIMATION_DAMAGE);
        // Set the health to 0 if the damage is greater than the health
        player.setHealth(finalHealth);

        return damage.amount();
    }

    @NotNull
    public Location getAudioPlayLocation() {
        return player.getLocation();
    }

    @NotNull
    public ItemStack getHeldItem() {
        return player.getInventory().getItemInMainHand();
    }

    public void setHeldItem(@Nullable ItemStack itemStack) {
        player.getInventory().setItemInMainHand(itemStack);
    }

    @NotNull
    public List<Block> getLastTwoTargetBlocks(int maxDistance) {
        return player.getLastTwoTargetBlocks(null, maxDistance);
    }

    @NotNull
    public Location getShootingDirection() {
        return player.getEyeLocation().subtract(0, 0.25, 0);
    }

    @NotNull
    public Location getThrowingDirection() {
        return player.getEyeLocation();
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

    public boolean isImmuneTo(@NotNull DamageType damageType) {
        return false;
    }

    public void modifyCameraRotation(float yaw, float pitch) {
        internals.setPlayerRotation(player, yaw, pitch);
    }

    public void removeItem(@NotNull ItemStack itemStack) {
        player.getInventory().removeItem(itemStack);
    }
}
