package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.item.effect.Effect;
import nl.matsgemmeke.battlegrounds.item.effect.EffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.StaticSource;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FireballLauncher implements ProjectileLauncher {

    private static final double FINDING_RANGE_DEPLOYMENT_OBJECTS = 0.3;
    private static final double FINDING_RANGE_ENTITIES = 0.1;
    private static final long SCHEDULE_DELAY = 0L;
    private static final long SCHEDULE_INTERVAL = 1L;

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final CollisionDetector collisionDetector;
    @NotNull
    private final Effect effect;
    @NotNull
    private final FireballProperties properties;
    @NotNull
    private final ParticleEffectSpawner particleEffectSpawner;
    @NotNull
    private final Scheduler scheduler;
    @NotNull
    private final TargetFinder targetFinder;

    @Inject
    public FireballLauncher(
            @NotNull ParticleEffectSpawner particleEffectSpawner,
            @NotNull Scheduler scheduler,
            @Assisted @NotNull FireballProperties properties,
            @Assisted @NotNull AudioEmitter audioEmitter,
            @Assisted @NotNull CollisionDetector collisionDetector,
            @Assisted @NotNull Effect effect,
            @Assisted @NotNull TargetFinder targetFinder
    ) {
        this.particleEffectSpawner = particleEffectSpawner;
        this.scheduler = scheduler;
        this.properties = properties;
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
        this.effect = effect;
        this.targetFinder = targetFinder;
    }

    public void launch(LaunchContext context) {
        Entity entity = context.entity();
        Location initiationLocation = entity.getLocation();
        Vector velocity = context.direction().getDirection().multiply(properties.velocity());
        ProjectileLaunchSource source = context.source();

        audioEmitter.playSounds(properties.shotSounds(), initiationLocation);

        Fireball fireball = source.launchProjectile(SmallFireball.class, velocity);
        fireball.setIsIncendiary(false);
        fireball.setYield(0.0f);

        Schedule schedule = scheduler.createRepeatingSchedule(SCHEDULE_DELAY, SCHEDULE_INTERVAL);
        schedule.addTask(() -> this.displayParticleEffect(fireball));
        schedule.addTask(() -> this.processProjectileStep(entity, initiationLocation, fireball, schedule));
        schedule.start();
    }

    private void displayParticleEffect(Fireball fireball) {
        Location location = fireball.getLocation();

        properties.getTrajectoryParticleEffect().ifPresent(particleEffect -> {
            particleEffectSpawner.spawnParticleEffect(particleEffect, location);
        });
    }

    private void processProjectileStep(Entity entity, Location initiationLocation, Fireball fireball, Schedule schedule) {
        Location fireballLocation = fireball.getLocation();
        World fireballWorld = fireballLocation.getWorld();
        Location nextLocation = fireballLocation.clone().add(fireballLocation.getDirection());

        if (collisionDetector.producesBlockCollisionAt(nextLocation)) {
            this.activateEffect(entity, fireballLocation, fireballWorld, initiationLocation);
            schedule.stop();
            return;
        }

        TargetQuery query = this.createTargetQuery(entity.getUniqueId(), fireballLocation);

        if (targetFinder.containsTargets(query)) {
            this.activateEffect(entity, fireballLocation, fireballWorld, initiationLocation);
            schedule.stop();
        }
    }

    private void activateEffect(Entity entity, Location sourceLocation, World sourceWorld, Location initiationLocation) {
        StaticSource source = new StaticSource(sourceLocation, sourceWorld);
        EffectContext context = new EffectContext(entity, source, initiationLocation);

        effect.prime(context);
        effect.activateInstantly();
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
