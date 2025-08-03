package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.Hitbox;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultFirearm extends BaseGun implements Firearm {

    private static final double DEPLOYMENT_OBJECT_FINDING_RANGE = 0.3;
    private static final double ENTITY_FINDING_RANGE = 0.1;
    private static final double PROJECTILE_DISTANCE_JUMP = 0.5;
    private static final double PROJECTILE_DISTANCE_START = 0.5;
    private static final DustOptions DEFAULT_PARTICLE_COLOR = new DustOptions(Color.WHITE, 1);

    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private CollisionDetector collisionDetector;
    @NotNull
    private DamageProcessor damageProcessor;
    private double headshotDamageMultiplier;
    private List<GameSound> shotSounds;
    private List<GameSound> triggerSounds;
    @Nullable
    private SpreadPattern spreadPattern;
    @NotNull
    private TargetFinder targetFinder;

    public DefaultFirearm(@NotNull String id, @NotNull AudioEmitter audioEmitter, @NotNull CollisionDetector collisionDetector, @NotNull DamageProcessor damageProcessor, @NotNull TargetFinder targetFinder) {
        super(id);
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
        this.damageProcessor = damageProcessor;
        this.targetFinder = targetFinder;
    }

    public double getHeadshotDamageMultiplier() {
        return headshotDamageMultiplier;
    }

    public void setHeadshotDamageMultiplier(double headshotDamageMultiplier) {
        this.headshotDamageMultiplier = headshotDamageMultiplier;
    }

    @NotNull
    public List<GameSound> getShotSounds() {
        return shotSounds;
    }

    public void setShotSounds(@NotNull List<GameSound> shotSounds) {
        this.shotSounds = shotSounds;
    }

    @Nullable
    public SpreadPattern getSpreadPattern() {
        return spreadPattern;
    }

    public void setSpreadPattern(@Nullable SpreadPattern spreadPattern) {
        this.spreadPattern = spreadPattern;
    }

    public List<GameSound> getTriggerSounds() {
        return triggerSounds;
    }

    public void setTriggerSounds(List<GameSound> triggerSounds) {
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
        double distance = startingLocation.distance(targetLocation);
        double damage = rangeProfile.getDamageByDistance(distance);
        Hitbox hitbox = Hitbox.getHitbox(targetLocation.getY(), projectileLocation.getY());

        if (hitbox == Hitbox.HEAD) {
            damage *= headshotDamageMultiplier;
        }

        return damage;
    }

    private boolean inflictDamage(@NotNull Location startingLocation, @NotNull Location projectileLocation) {
        for (GameEntity target : targetFinder.findTargets(holder.getEntity().getUniqueId(), projectileLocation, ENTITY_FINDING_RANGE)) {
            if (target.getEntity() == holder.getEntity()) {
                continue;
            }

            Location targetLocation = target.getEntity().getLocation();

            double damageAmount = this.getDamage(startingLocation, targetLocation, projectileLocation);
            Damage damage = new Damage(damageAmount, DamageType.BULLET_DAMAGE);

            target.damage(damage);
            return true;
        }

        for (DeploymentObject deploymentObject : targetFinder.findDeploymentObjects(holder.getEntity().getUniqueId(), projectileLocation, DEPLOYMENT_OBJECT_FINDING_RANGE)) {
            Location objectLocation = deploymentObject.getLocation();

            double damageAmount = this.getDamage(startingLocation, objectLocation, projectileLocation);
            Damage damage = new Damage(damageAmount, DamageType.BULLET_DAMAGE);

            damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);
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
        return ammunitionStorage.getMagazineAmmo() > 0;
    }

    public boolean shoot() {
        if (holder == null) {
            return false;
        }

        Location direction = holder.getShootingDirection();

        ammunitionStorage.setMagazineAmmo(ammunitionStorage.getMagazineAmmo() - 1);
        audioEmitter.playSounds(shotSounds, direction);

        if (recoil != null) {
            direction = recoil.produceRecoil(holder, direction);
        }

        for (Location projectileDirection : this.getProjectileDirections(direction)) {
            this.shootProjectile(projectileDirection);
        }

        this.update();
        holder.setHeldItem(itemStack);
        return true;
    }

    private void shootProjectile(@NotNull Location direction) {
        double distance = PROJECTILE_DISTANCE_START;
        double projectileRange = rangeProfile.longRangeDistance();

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
        } while (distance < projectileRange);
    }

    private Iterable<Location> getProjectileDirections(@NotNull Location shootingDirection) {
        if (spreadPattern != null) {
            return spreadPattern.getShotDirections(shootingDirection);
        } else {
            return List.of(shootingDirection);
        }
    }
}
