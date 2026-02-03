package nl.matsgemmeke.battlegrounds.entity;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSourceType;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.Matchable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DefaultGamePlayer implements GamePlayer {

    private static final double DAMAGE_ANIMATION_DAMAGE = 0.001;
    private static final float NORMAL_ACCURACY = 1.0f;
    private static final float SNEAKING_ACCURACY = 2.0f;
    private static final float SPRINTING_ACCURACY = 0.5f;
    private static final int OPERATING_FOOD_LEVEL = 6;

    private final HitboxProvider<Player> hitboxProvider;
    private final InternalsProvider internals;
    private final Player player;
    private final Set<ItemEffect> effects;
    private boolean canDeploy;
    private boolean passive;
    @Nullable
    private Damage lastDamage;
    private int previousFoodLevel;

    @Inject
    public DefaultGamePlayer(InternalsProvider internals, @Assisted Player player, @Assisted HitboxProvider<Player> hitboxProvider) {
        this.player = player;
        this.hitboxProvider = hitboxProvider;
        this.internals = internals;
        this.effects = new HashSet<>();
        this.canDeploy = true;
        this.passive = false;
    }

    public boolean canDeploy() {
        return canDeploy;
    }

    public void setCanDeploy(boolean canDeploy) {
        this.canDeploy = canDeploy;
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

    @Nullable
    public Damage getLastDamage() {
        return lastDamage;
    }

    @NotNull
    public Location getLocation() {
        return player.getLocation();
    }

    @NotNull
    public String getName() {
        return player.getName();
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public Vector getVelocity() {
        return player.getVelocity();
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

    @Override
    public boolean isValid() {
        return player.isValid();
    }

    @Override
    public void addPotionEffect(PotionEffect potionEffect) {
        player.addPotionEffect(potionEffect);
    }

    @Override
    public Optional<PotionEffect> getPotionEffect(PotionEffectType potionEffectType) {
        return Optional.ofNullable(player.getPotionEffect(potionEffectType));
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {
        player.removePotionEffect(potionEffectType);
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

        lastDamage = damage;

        // Divide by 5 to convert to hearts value
        double finalHealth = Math.max(player.getHealth() - damage.amount() / 5, 0.0);

        // Create fake damage animation
        player.damage(DAMAGE_ANIMATION_DAMAGE);
        // Set the health to 0 if the damage is greater than the health
        player.setHealth(finalHealth);

        return damage.amount();
    }

    @Override
    public float getAttackStrength() {
        return player.getAttackCooldown();
    }

    @NotNull
    public Location getAudioPlayLocation() {
        return player.getLocation();
    }

    @Override
    public DamageSourceType getDamageSourceType() {
        return DamageSourceType.PLAYER;
    }

    @NotNull
    public Location getDeployLocation() {
        return player.getEyeLocation();
    }

    @NotNull
    public ItemStack getHeldItem() {
        return player.getInventory().getItemInMainHand();
    }

    public void setHeldItem(@Nullable ItemStack itemStack) {
        player.getInventory().setItemInMainHand(itemStack);
    }

    @Override
    public Hitbox getHitbox() {
        return hitboxProvider.provideHitbox(player);
    }

    @NotNull
    public Optional<Integer> getItemSlot(@NotNull Matchable item) {
        Inventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack itemStack = contents[slot];
            if (itemStack != null && item.isMatching(itemStack)) {
                return Optional.of(slot);
            }
        }

        return Optional.empty();
    }

    @NotNull
    public List<Block> getLastTwoTargetBlocks(int maxDistance) {
        return player.getLastTwoTargetBlocks(null, maxDistance);
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

    @Override
    public Location getShootingDirection() {
        return player.getEyeLocation().subtract(0, 0.25, 0);
    }

    @Override
    public Location getThrowDirection() {
        return player.getEyeLocation();
    }

    public boolean isImmuneTo(@NotNull DamageType damageType) {
        return false;
    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> projectileClass, Vector velocity) {
        return player.launchProjectile(projectileClass, velocity);
    }

    public void modifyCameraRotation(float yaw, float pitch) {
        internals.setPlayerRotation(player, yaw, pitch);
    }

    @Override
    public void playSound(Location location, GameSound sound) {
        player.playSound(location, sound.getSound(), sound.getVolume(), sound.getPitch());
    }

    public void removeItem(@NotNull ItemStack itemStack) {
        player.getInventory().removeItem(itemStack);
    }
}
