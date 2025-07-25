package nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.Effect;
import nl.matsgemmeke.battlegrounds.item.effect.EffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.StaticSource;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BulletLauncher implements ProjectileLauncher {

    private static final double DISTANCE_JUMP = 0.5;
    private static final double DISTANCE_START = 0.5;
    private static final double FINDING_RANGE_DEPLOYMENT_OBJECTS = 0.3;
    private static final double FINDING_RANGE_ENTITIES = 0.1;

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final BulletProperties properties;
    @NotNull
    private final CollisionDetector collisionDetector;
    @NotNull
    private final Effect effect;
    @NotNull
    private final ParticleEffectSpawner particleEffectSpawner;
    @NotNull
    private final TargetFinder targetFinder;

    @Inject
    public BulletLauncher(
            @NotNull ParticleEffectSpawner particleEffectSpawner,
            @Assisted @NotNull BulletProperties properties,
            @Assisted @NotNull AudioEmitter audioEmitter,
            @Assisted @NotNull CollisionDetector collisionDetector,
            @Assisted @NotNull Effect effect,
            @Assisted @NotNull TargetFinder targetFinder
    ) {
        this.particleEffectSpawner = particleEffectSpawner;
        this.properties = properties;
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
        this.effect = effect;
        this.targetFinder = targetFinder;
    }

    public void launch(@NotNull LaunchContext context) {
        boolean hit;
        double distance = DISTANCE_START;
        double projectileRange = 50.0;

        Location startingLocation = context.direction();
        Entity entity = context.entity();

        audioEmitter.playSounds(properties.shotSounds(), startingLocation);

        do {
            Vector vector = startingLocation.getDirection().multiply(distance);
            Location projectileLocation = startingLocation.clone().add(vector);
            hit = this.processProjectileStep(entity, startingLocation, projectileLocation);
            distance += DISTANCE_JUMP;
        } while (!hit && distance < projectileRange);
    }

    private boolean processProjectileStep(Entity entity, Location startingLocation, Location projectileLocation) {
        ParticleEffect trajectoryParticleEffect = properties.trajectoryParticleEffect();

        if (trajectoryParticleEffect != null) {
            particleEffectSpawner.spawnParticleEffect(trajectoryParticleEffect, projectileLocation);
        }

        // Check if the projectile's current location causes a collision
        if (collisionDetector.producesBlockCollisionAt(projectileLocation)) {
            Block block = projectileLocation.getBlock();
            block.getWorld().playEffect(projectileLocation, org.bukkit.Effect.STEP_SOUND, block.getType());
            return true;
        }

        TargetQuery query = this.createTargetQuery(entity.getUniqueId(), projectileLocation);

        if (targetFinder.containsTargets(query)) {
            World world = projectileLocation.getBlock().getWorld();
            StaticSource source = new StaticSource(projectileLocation, world);
            EffectContext context = new EffectContext(entity, source, startingLocation);

            effect.prime(context);
            effect.activateInstantly();
            return true;
        }

        return false;
    }

    private TargetQuery createTargetQuery(UUID entityId, Location location) {
        return new TargetQuery()
                .forEntity(entityId)
                .forLocation(location)
                .withRange(TargetType.ENTITY, FINDING_RANGE_ENTITIES)
                .withRange(TargetType.DEPLOYMENT_OBJECT, FINDING_RANGE_DEPLOYMENT_OBJECTS)
                .enemiesOnly(true);
    }
}
