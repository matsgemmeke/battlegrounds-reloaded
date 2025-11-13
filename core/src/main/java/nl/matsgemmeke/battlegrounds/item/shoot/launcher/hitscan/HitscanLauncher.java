package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.StaticSource;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HitscanLauncher implements ProjectileLauncher {

    private static final double DISTANCE_JUMP = 0.1;
    private static final double DISTANCE_START = 0.5;
    /**
     * The distance interval (in blocks) at which particles are spawned along the hitscan path.
     */
    private static final double PARTICLE_STEP = 0.5;
    private static final double FINDING_RANGE_DEPLOYMENT_OBJECTS = 0.3;
    private static final double FINDING_RANGE_ENTITIES = 0.1;

    private final AudioEmitter audioEmitter;
    private final CollisionDetector collisionDetector;
    private final HitscanProperties properties;
    private final ItemEffect itemEffect;
    private final ParticleEffectSpawner particleEffectSpawner;
    private final Scheduler scheduler;
    private final Set<Schedule> soundPlaySchedules;
    private final TargetFinder targetFinder;

    @Inject
    public HitscanLauncher(
            AudioEmitter audioEmitter,
            CollisionDetector collisionDetector,
            ParticleEffectSpawner particleEffectSpawner,
            Scheduler scheduler,
            TargetFinder targetFinder,
            @Assisted HitscanProperties properties,
            @Assisted ItemEffect itemEffect
    ) {
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
        this.particleEffectSpawner = particleEffectSpawner;
        this.scheduler = scheduler;
        this.targetFinder = targetFinder;
        this.properties = properties;
        this.itemEffect = itemEffect;
        this.soundPlaySchedules = new HashSet<>();
    }

    @Override
    public void cancel() {
        soundPlaySchedules.forEach(Schedule::stop);
    }

    @Override
    public void launch(@NotNull LaunchContext context) {
        boolean hit;
        double distance = DISTANCE_START;
        double projectileRange = 50.0;

        Location startingLocation = context.direction();
        Vector direction = startingLocation.getDirection();
        Entity entity = context.entity();

        this.scheduleSoundPlayTasks(properties.shotSounds(), entity);

        do {
            Vector vector = direction.clone().multiply(distance);
            Location projectileLocation = startingLocation.clone().add(vector);
            hit = this.processProjectileStep(entity, projectileLocation, distance);
            distance += DISTANCE_JUMP;
        } while (!hit && distance < projectileRange);
    }

    private void scheduleSoundPlayTasks(List<GameSound> sounds, Entity entity) {
        for (GameSound sound : sounds) {
            Schedule schedule = scheduler.createSingleRunSchedule(sound.getDelay());
            schedule.addTask(() -> audioEmitter.playSound(sound, entity.getLocation()));
            schedule.start();

            soundPlaySchedules.add(schedule);
        }
    }

    private boolean processProjectileStep(Entity entity, Location projectileLocation, double distance) {
        ParticleEffect trajectoryParticleEffect = properties.trajectoryParticleEffect();

        if (trajectoryParticleEffect != null && distance % PARTICLE_STEP == 0) {
            particleEffectSpawner.spawnParticleEffect(trajectoryParticleEffect, projectileLocation);
        }

        // Check if the projectile's current location causes a collision
        if (collisionDetector.producesBlockCollisionAt(projectileLocation)) {
            Block block = projectileLocation.getBlock();
            block.getWorld().playEffect(projectileLocation, org.bukkit.Effect.STEP_SOUND, block.getType());

            this.startPerformance(entity, projectileLocation);
            return true;
        }

        TargetQuery query = this.createTargetQuery(entity.getUniqueId(), projectileLocation);

        if (targetFinder.containsTargets(query)) {
            this.startPerformance(entity, projectileLocation);
            return true;
        }

        return false;
    }

    private void startPerformance(Entity entity, Location projectileLocation) {
        Location sourceLocation = projectileLocation.clone();
        World world = projectileLocation.getBlock().getWorld();
        StaticSource source = new StaticSource(sourceLocation, world);

        ItemEffectContext context = new ItemEffectContext(entity, source, projectileLocation);

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
