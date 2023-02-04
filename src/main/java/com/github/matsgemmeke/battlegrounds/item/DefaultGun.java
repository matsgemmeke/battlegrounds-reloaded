package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.entity.Hitbox;
import com.github.matsgemmeke.battlegrounds.item.mechanism.FiringMode;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DefaultGun extends AbstractFirearm implements Gun {

    private double headshotDamageMultiplier;
    private FiringMode firingMode;
    private int magazineAmmo;
    private Iterable<BattleSound> shotSounds;
    private Iterable<BattleSound> triggerSounds;

    public DefaultGun(
            @NotNull String id,
            @NotNull String name,
            @NotNull String description,
            @NotNull BattleContext context
    ) {
        super(id, name, description, context);
    }

    public double getHeadshotDamageMultiplier() {
        return headshotDamageMultiplier;
    }

    public void setHeadshotDamageMultiplier(double headshotDamageMultiplier) {
        this.headshotDamageMultiplier = headshotDamageMultiplier;
    }

    public FiringMode getFiringMode() {
        return firingMode;
    }

    public void setFiringMode(FiringMode firingMode) {
        this.firingMode = firingMode;
    }

    public int getMagazineAmmo() {
        return magazineAmmo;
    }

    public void setMagazineAmmo(int magazineAmmo) {
        this.magazineAmmo = magazineAmmo;
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
        DustOptions dustOptions = new DustOptions(Color.WHITE, 1);
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0.0, 0.0, 0.0, 0.0, dustOptions);
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

    private boolean inflictDamage(@NotNull BattleEntity holder, @NotNull Location projectileLocation) {
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

    public void onLeftClick(@NotNull BattleEntity battleEntity) {
        this.reload();
    }

    public void onRightClick(@NotNull BattleEntity battleEntity) {
        if (magazineAmmo <= 0) {
            context.playSounds(triggerSounds, battleEntity.getEntity().getLocation());
            return;
        }

        firingMode.activate();
    }

    public void reload() {
        System.out.println("reload");
    }

    public void shoot() {
        if (holder == null) {
            return;
        }

        // Place the shooting at the height of the firearm
        Location direction = holder.getEntity().getEyeLocation().subtract(0, 0.25, 0);
        // Calculate the direction of the projectile
        Location shootingDirection = this.getShootingDirection(direction, holder.getRelativeAccuracy());

        this.shootProjectile(holder, shootingDirection);
    }

    private void shootProjectile(@NotNull BattleEntity holder, @NotNull Location direction) {
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
                block.getWorld().playEffect(direction, Effect.STEP_SOUND, direction.getBlock().getType());
                return;
            }

            // Check if the projectile has hit an enemy entity
            if (this.inflictDamage(holder, direction)) {
                return;
            }

            this.displayParticle(direction);

            direction.subtract(vector);

            distance += distanceJump;
        } while (distance < longRange);
    }

    public boolean update() {
        return false;
    }
}
