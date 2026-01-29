package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.targeting.condition.HitboxTargetCondition;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.item.actor.StaticActor;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class HitscanLauncher implements ProjectileLauncher {

    private static final double DISTANCE_START = 0.5;
    private static final double DISTANCE_STEP = 0.1;
    private static final int MAX_STEPS = 1000;
    // The distance interval (in blocks) at which particles are spawned along the hitscan path.
    private static final double PARTICLE_PER_STEPS = 5;
    private static final double FINDING_RANGE_DEPLOYMENT_OBJECTS = 0.2;
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
    public void launch(LaunchContext context) {
        boolean hit;
        int steps = 0;

        DamageSource damageSource = context.damageSource();
        Location startingLocation = context.direction();
        World world = context.world();
        Vector direction = startingLocation.getDirection();

        this.scheduleSoundPlayTasks(properties.launchSounds(), context.soundLocationSupplier());

        do {
            hit = this.processProjectileStep(damageSource, startingLocation, world, direction, steps);
            steps++;
        } while (!hit && steps < MAX_STEPS);
    }

    private void scheduleSoundPlayTasks(List<GameSound> sounds, Supplier<Location> locationSupplier) {
        for (GameSound sound : sounds) {
            Schedule schedule = scheduler.createSingleRunSchedule(sound.getDelay());
            schedule.addTask(() -> audioEmitter.playSound(sound, locationSupplier.get()));
            schedule.start();

            soundPlaySchedules.add(schedule);
        }
    }

    private boolean processProjectileStep(DamageSource damageSource, Location startingLocation, World world, Vector direction, int steps) {
        double distance = DISTANCE_START + steps * DISTANCE_STEP;

        Vector vector = direction.clone().multiply(distance);
        Location projectileLocation = startingLocation.clone().add(vector);
        ParticleEffect trajectoryParticleEffect = properties.trajectoryParticleEffect();

        if (trajectoryParticleEffect != null && steps % PARTICLE_PER_STEPS == 0) {
            particleEffectSpawner.spawnParticleEffect(trajectoryParticleEffect, projectileLocation);
        }

        // Check if the projectile's current location causes a collision
        if (collisionDetector.producesBlockCollisionAt(projectileLocation)) {
            Block block = projectileLocation.getBlock();
            block.getWorld().playEffect(projectileLocation, org.bukkit.Effect.STEP_SOUND, block.getType());

            Location hitLocation = projectileLocation.clone();
            CollisionResult collisionResult = new CollisionResult(block, null, hitLocation);

            this.startPerformance(collisionResult, damageSource, projectileLocation, world);
            return true;
        }

        UUID sourceId = damageSource.getUniqueId();
        TargetQuery query = this.createTargetQuery(sourceId, projectileLocation);
        List<DamageTarget> targets = targetFinder.findTargets(query);

        if (!targets.isEmpty()) {
            DamageTarget hitTarget = targets.get(0);
            Location hitLocation = projectileLocation.clone();
            CollisionResult collisionResult = new CollisionResult(null, hitTarget, hitLocation);

            this.startPerformance(collisionResult, damageSource, hitLocation, world);
            return true;
        }

        return false;
    }

    private void startPerformance(CollisionResult collisionResult, DamageSource damageSource, Location startingLocation, World world) {
        StaticActor actor = new StaticActor(startingLocation, world);
        ItemEffectContext context = new ItemEffectContext(collisionResult, damageSource, actor, null, startingLocation);

        itemEffect.startPerformance(context);
    }

    private TargetQuery createTargetQuery(UUID uniqueId, Location location) {
        return new TargetQuery()
                .uniqueId(uniqueId)
                .location(location)
                .conditions(new HitboxTargetCondition())
                .enemiesOnly(true);
    }
}
