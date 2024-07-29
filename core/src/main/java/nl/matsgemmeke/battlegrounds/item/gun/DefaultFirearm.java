package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.Hitbox;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultFirearm extends BaseGun implements Firearm {

    private static final double ENTITY_FINDING_RANGE = 0.1;
    private static final double PROJECTILE_DISTANCE_JUMP = 0.5;
    private static final double PROJECTILE_DISTANCE_START = 0.5;
    private static final DustOptions DEFAULT_PARTICLE_COLOR = new DustOptions(Color.WHITE, 1);

    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private CollisionDetector collisionDetector;
    private double headshotDamageMultiplier;
    private FireMode fireMode;
    private int magazineAmmo;
    private int magazineSize;
    private int reserveAmmo;
    private Iterable<GameSound> shotSounds;
    private Iterable<GameSound> triggerSounds;
    @Nullable
    private SpreadPattern spreadPattern;

    public DefaultFirearm(@NotNull AudioEmitter audioEmitter, @NotNull CollisionDetector collisionDetector) {
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
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

    public Iterable<GameSound> getShotSounds() {
        return shotSounds;
    }

    @Nullable
    public SpreadPattern getSpreadPattern() {
        return spreadPattern;
    }

    public void setSpreadPattern(@Nullable SpreadPattern spreadPattern) {
        this.spreadPattern = spreadPattern;
    }

    public void setShotSounds(Iterable<GameSound> shotSounds) {
        this.shotSounds = shotSounds;
    }

    public Iterable<GameSound> getTriggerSounds() {
        return triggerSounds;
    }

    public void setTriggerSounds(Iterable<GameSound> triggerSounds) {
        this.triggerSounds = triggerSounds;
    }

    private void displayParticle(@NotNull Location location) {
        World world = location.getWorld();

        if (world == null) {
            return;
        }

        world.spawnParticle(Particle.REDSTONE, location, 1, 0.0, 0.0, 0.0, 0.0, DEFAULT_PARTICLE_COLOR);
    }

    private double getDamage(@NotNull Location startingLocation, @NotNull Location targetLocation, @NotNull Location projectileLocation) {
        double damage = 0.0;
        double distance = startingLocation.distance(targetLocation);

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

    private boolean inflictDamage(@NotNull Location startingLocation, @NotNull Location projectileLocation) {
        for (GameEntity target : collisionDetector.findTargets(holder, projectileLocation, ENTITY_FINDING_RANGE)) {
            if (target.getEntity() == holder.getEntity()) {
                continue;
            }

            Location targetLocation = target.getEntity().getLocation();

            double damage = this.getDamage(startingLocation, targetLocation, projectileLocation);
            target.damage(damage);
            return true;
        }

        return false;
    }

    public void onChangeFrom() {
        controls.cancelAllFunctions();

        if (holder == null) {
            return;
        }

        controls.performAction(Action.CHANGE_FROM, holder);
    }

    public void onChangeTo() {
        // TODO some feature
    }

    public void onLeftClick() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.LEFT_CLICK, holder);
    }

    public void onRightClick() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.RIGHT_CLICK, holder);
    }

    public void onSwapFrom() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.SWAP_FROM, holder);
    }

    public boolean canShoot() {
        return magazineAmmo > 0;
    }

    public boolean shoot() {
        if (holder == null) {
            return false;
        }

        Location direction = holder.getShootingDirection();

        magazineAmmo--;
        audioEmitter.playSounds(shotSounds, direction);

        if (recoilProducer != null) {
            direction = recoilProducer.produceRecoil(holder, direction);
        }

        for (Location projectileDirection : this.getProjectileDirections(direction)) {
            this.shootProjectile(projectileDirection);
        }

        this.update();
        return true;
    }

    private void shootProjectile(@NotNull Location direction) {
        double distance = PROJECTILE_DISTANCE_START;

        // Keep reference to starting point
        Location startingPoint = direction.clone();

        do {
            Vector vector = direction.getDirection().multiply(distance);
            direction.add(vector);

            // Check if the projectile's current location causes a collision
            if (collisionDetector.producesBlockCollisionAt(direction)) {
                Block block = direction.getBlock();
                block.getWorld().playEffect(direction, Effect.STEP_SOUND, block.getType());
                break;
            }

            // Check if the projectile has hit an enemy entity
            if (this.inflictDamage(startingPoint, direction)) {
                break;
            }

            this.displayParticle(direction);

            direction.subtract(vector);

            distance += PROJECTILE_DISTANCE_JUMP;
        } while (distance < longRange);
    }

    private Iterable<Location> getProjectileDirections(@NotNull Location aimDirection) {
        if (spreadPattern != null) {
            return spreadPattern.getProjectileDirections(aimDirection);
        } else {
            return List.of(aimDirection);
        }
    }
}
