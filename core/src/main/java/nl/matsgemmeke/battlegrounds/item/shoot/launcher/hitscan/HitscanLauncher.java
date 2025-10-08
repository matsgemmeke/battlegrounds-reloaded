package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectNew;
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

public class HitscanLauncher implements ProjectileLauncher {

    private static final double DISTANCE_JUMP = 0.5;
    private static final double DISTANCE_START = 0.5;
    private static final double FINDING_RANGE_DEPLOYMENT_OBJECTS = 0.3;
    private static final double FINDING_RANGE_ENTITIES = 0.1;

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final CollisionDetector collisionDetector;
    @NotNull
    private final HitscanProperties properties;
    @NotNull
    private final ItemEffectNew itemEffect;
    @NotNull
    private final ParticleEffectSpawner particleEffectSpawner;
    @NotNull
    private final TargetFinder targetFinder;

    @Inject
    public HitscanLauncher(
            @NotNull AudioEmitter audioEmitter,
            @NotNull CollisionDetector collisionDetector,
            @NotNull ParticleEffectSpawner particleEffectSpawner,
            @NotNull TargetFinder targetFinder,
            @Assisted @NotNull HitscanProperties properties,
            @Assisted @NotNull ItemEffectNew itemEffect
    ) {
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
        this.particleEffectSpawner = particleEffectSpawner;
        this.targetFinder = targetFinder;
        this.properties = properties;
        this.itemEffect = itemEffect;
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

            this.startPerformance(entity, projectileLocation, startingLocation);
            return true;
        }

        TargetQuery query = this.createTargetQuery(entity.getUniqueId(), projectileLocation);

        if (targetFinder.containsTargets(query)) {
            this.startPerformance(entity, projectileLocation, startingLocation);
            return true;
        }

        return false;
    }

    private void startPerformance(Entity entity, Location projectileLocation, Location startingLocation) {
        Location sourceLocation = projectileLocation.clone();
        World world = projectileLocation.getBlock().getWorld();
        StaticSource source = new StaticSource(sourceLocation, world);

        ItemEffectContext context = new ItemEffectContext(entity, source, startingLocation);

        itemEffect.startPerformance(context);
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
