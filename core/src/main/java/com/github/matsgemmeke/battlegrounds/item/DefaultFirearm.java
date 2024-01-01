package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.entity.Hitbox;
import com.github.matsgemmeke.battlegrounds.item.mechanics.FireMode;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DefaultFirearm extends AbstractGun implements Firearm {

    private static final DustOptions defaultParticleColor = new DustOptions(Color.WHITE, 1);

    private double headshotDamageMultiplier;
    private FireMode fireMode;
    private int magazineAmmo;
    private int magazineSize;
    private int reserveAmmo;
    private Iterable<BattleSound> shotSounds;
    private Iterable<BattleSound> triggerSounds;

    public DefaultFirearm(@NotNull BattleContext context) {
        super(context);
    }

    public double getHeadshotDamageMultiplier() {
        return headshotDamageMultiplier;
    }

    public void setHeadshotDamageMultiplier(double headshotDamageMultiplier) {
        this.headshotDamageMultiplier = headshotDamageMultiplier;
    }

    public FireMode getFireMode() {
        return fireMode;
    }

    public void setFireMode(FireMode fireMode) {
        this.fireMode = fireMode;
    }

    public int getMagazineAmmo() {
        return magazineAmmo;
    }

    public void setMagazineAmmo(int magazineAmmo) {
        this.magazineAmmo = magazineAmmo;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public void setMagazineSize(int magazineSize) {
        this.magazineSize = magazineSize;
    }

    public int getReserveAmmo() {
        return reserveAmmo;
    }

    public void setReserveAmmo(int reserveAmmo) {
        this.reserveAmmo = reserveAmmo;
    }

    public Iterable<BattleSound> getShotSounds() {
        return shotSounds;
    }

    public void setShotSounds(Iterable<BattleSound> shotSounds) {
        this.shotSounds = shotSounds;
    }

    public Iterable<BattleSound> getTriggerSounds() {
        return triggerSounds;
    }

    public void setTriggerSounds(Iterable<BattleSound> triggerSounds) {
        this.triggerSounds = triggerSounds;
    }

    private void displayParticle(@NotNull Location location) {
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0.0, 0.0, 0.0, 0.0, defaultParticleColor);
    }

    private double getDamage(@NotNull Location holderLocation, @NotNull Location targetLocation, @NotNull Location projectileLocation) {
        double damage = 0.0;
        double distance = holderLocation.distance(targetLocation);

        if (distance <= shortRange) {
            damage = shortDamage;
        } else if (distance <= mediumRange) {
            damage = mediumDamage;
        } else if (distance <= longRange) {
            damage = longDamage;
        }

        Hitbox hitbox = Hitbox.getHitbox(targetLocation.getY(), projectileLocation.getY());

        if (hitbox == Hitbox.HEAD) {
            damage *= headshotDamageMultiplier;
        }

        return damage;
    }

    @NotNull
    protected String getItemDisplayName() {
        return ChatColor.WHITE + name + " " + magazineAmmo + "/" + reserveAmmo;
    }

    private boolean inflictDamage(@NotNull BattleItemHolder holder, @NotNull Location projectileLocation) {
        double range = 0.1;

        for (BattleEntity target : context.getTargets(holder, projectileLocation, range)) {
            Location holderLocation = holder.getEntity().getLocation();
            Location targetLocation = target.getEntity().getLocation();

            double damage = this.getDamage(holderLocation, targetLocation, projectileLocation);
            target.damage(damage);
            return true;
        }

        return false;
    }

    public void onChangeHeldItem(@NotNull BattleItemHolder holder) {
        if (currentOperatingMode == null) {
            return;
        }

        currentOperatingMode.cancel(holder);
    }

    public void onLeftClick(@NotNull BattleItemHolder holder) {
        // Do not do anything if the firearm is currently being operated
        if (currentOperatingMode != null) {
            return;
        }

        // If the firearm is using a scope then stop using the scope istead of reloading
        if (scopeAttachment != null && scopeAttachment.isScoped()) {
            scopeAttachment.removeEffect();
            return;
        }

        // If the magazine is already full or there is no reserve ammo, do not perform a reload
        if (magazineAmmo >= magazineSize || reserveAmmo <= 0) {
            return;
        }

        this.reload();
    }

    public void onRightClick(@NotNull BattleItemHolder holder) {
        if (currentOperatingMode != null) {
            return;
        }

        if (scopeAttachment != null && !scopeAttachment.isScoped()) {
            scopeAttachment.applyEffect(holder);
            return;
        }

        if (magazineAmmo <= 0) {
            context.playSounds(triggerSounds, holder.getEntity().getLocation());
            return;
        }

        fireMode.activate(holder);
    }

    public boolean shoot() {
        if (holder == null) {
            return false;
        }

        // Place the shooting at the height of the firearm
        Location direction = holder.getEntity().getEyeLocation().subtract(0, 0.25, 0);

        if (recoilSystem != null) {
            direction = recoilSystem.produceRecoil(holder, direction);
        }

        this.shootProjectile(holder, direction);
        return true;
    }

    private void shootProjectile(@NotNull BattleItemHolder holder, @NotNull Location direction) {
        magazineAmmo--;

        context.playSounds(shotSounds, direction);

        double distance = 0.5;
        double distanceJump = 0.5;

        do {
            Vector vector = direction.getDirection().multiply(distance);
            direction.add(vector);

            // Check if the projectile's current location causes a collision
            if (context.producesCollisionAt(direction)) {
                Block block = direction.getBlock();
                block.getWorld().playEffect(direction, Effect.STEP_SOUND, block.getType());
                break;
            }

            // Check if the projectile has hit an enemy entity
            if (this.inflictDamage(holder, direction)) {
                break;
            }

            this.displayParticle(direction);

            direction.subtract(vector);

            distance += distanceJump;
        } while (distance < longRange);

        this.update();
    }
}
