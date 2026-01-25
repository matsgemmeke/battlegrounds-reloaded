package nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitResult;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.ProjectileActor;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultMapper;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class ArrowLauncher implements ProjectileLauncher {

    private static final double RAY_TRACE_LENGTH_MULTIPLIER = 2.0;
    private static final double BLOCK_CENTER_OFFSET = 0.5;

    private final ArrowProperties properties;
    private final AudioEmitter audioEmitter;
    private final CollisionResultMapper collisionResultMapper;
    private final GameEntityFinder gameEntityFinder;
    private final ItemEffect itemEffect;
    private final ProjectileHitActionRegistry projectileHitActionRegistry;
    private final ProjectileRegistry projectileRegistry;
    private final Scheduler scheduler;
    private final Set<Schedule> soundPlaySchedules;
    private final Set<TriggerExecutor> triggerExecutors;

    @Inject
    public ArrowLauncher(
            AudioEmitter audioEmitter,
            CollisionResultMapper collisionResultMapper,
            GameEntityFinder gameEntityFinder,
            ProjectileHitActionRegistry projectileHitActionRegistry,
            ProjectileRegistry projectileRegistry,
            Scheduler scheduler,
            @Assisted ArrowProperties properties,
            @Assisted ItemEffect itemEffect
    ) {
        this.audioEmitter = audioEmitter;
        this.collisionResultMapper = collisionResultMapper;
        this.gameEntityFinder = gameEntityFinder;
        this.projectileHitActionRegistry = projectileHitActionRegistry;
        this.projectileRegistry = projectileRegistry;
        this.scheduler = scheduler;
        this.properties = properties;
        this.itemEffect = itemEffect;
        this.soundPlaySchedules = new HashSet<>();
        this.triggerExecutors = new HashSet<>();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    @Override
    public void cancel() {
        soundPlaySchedules.forEach(Schedule::stop);
    }

    @Override
    public void launch(LaunchContext context) {
        DamageSource damageSource = context.damageSource();
        Location startingLocation = context.direction();
        Vector velocity = context.direction().getDirection().multiply(properties.velocity());
        ProjectileLaunchSource projectileSource = context.projectileSource();
        Supplier<Location> soundLocationSupplier = context.soundLocationSupplier();

        Arrow arrow = projectileSource.launchProjectile(Arrow.class, velocity);
        arrow.setDamage(0.0);
        arrow.setPickupStatus(PickupStatus.DISALLOWED);
        arrow.setVelocity(velocity);

        UUID sourceId = damageSource.getUniqueId();
        ProjectileActor actor = new ProjectileActor(arrow);
        TriggerContext triggerContext = new TriggerContext(sourceId, actor);

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);
            triggerRun.addObserver(triggerResult -> this.processTriggerResult(triggerResult, arrow, actor, damageSource, startingLocation));
            triggerRun.start();
        }

        projectileRegistry.register(arrow.getUniqueId());
        projectileHitActionRegistry.registerProjectileHitAction(arrow, projectileHitResult -> this.processProjectileHit(projectileHitResult, arrow, actor, damageSource, startingLocation));

        this.scheduleSoundPlayTasks(properties.launchSounds(), soundLocationSupplier);
    }

    private void processTriggerResult(TriggerResult triggerResult, Arrow arrow, Actor actor, DamageSource damageSource, Location startingLocation) {
        CollisionResult collisionResult = collisionResultMapper.map(triggerResult);
        ItemEffectContext context = new ItemEffectContext(collisionResult, damageSource, actor, null, startingLocation);

        itemEffect.startPerformance(context);
        projectileRegistry.unregister(arrow.getUniqueId());
        projectileHitActionRegistry.removeProjectileHitAction(arrow);

        if (collisionResult.getHitTarget().isPresent()) {
            arrow.remove();
        }
    }

    private void processProjectileHit(ProjectileHitResult projectileHitResult, Arrow arrow, Actor actor, DamageSource damageSource, Location startingLocation) {
        CollisionResult collisionResult = this.convertToCollisionResult(projectileHitResult, arrow);
        ItemEffectContext context = new ItemEffectContext(collisionResult, damageSource, actor, null, startingLocation);

        arrow.getWorld().spawnParticle(Particle.HEART, collisionResult.getHitLocation().get(), 1);

        itemEffect.startPerformance(context);
        projectileRegistry.unregister(arrow.getUniqueId());
        projectileHitActionRegistry.removeProjectileHitAction(arrow);

        if (collisionResult.getHitTarget().isPresent()) {
            arrow.remove();
        }
    }

    private CollisionResult convertToCollisionResult(ProjectileHitResult projectileHitResult, Arrow arrow) {
        Block hitBlock = projectileHitResult.hitBlock();
        Entity hitEntity = projectileHitResult.hitEntity();

        Location arrowLocation = arrow.getLocation();
        Vector arrowVelocity = arrow.getVelocity();
        double length = arrowVelocity.length() * RAY_TRACE_LENGTH_MULTIPLIER;
        World world = arrow.getWorld();

        if (hitEntity != null) {
            DamageTarget hitTarget = gameEntityFinder.findGameEntityByUniqueId(hitEntity.getUniqueId()).orElse(null);
            RayTraceResult rayTraceResult = world.rayTraceEntities(arrowLocation, arrowVelocity, length);

            if (rayTraceResult == null || rayTraceResult.getHitEntity() != hitEntity) {
                // When the ray trace cannot find a hit location, take the center of the hit entity
                BoundingBox boundingBox = hitEntity.getBoundingBox();
                Location hitLocation = hitEntity.getLocation().add(0, boundingBox.getMaxY() / 2, 0);

                return new CollisionResult(null, hitTarget, hitLocation);
            }

            Location hitLocation = rayTraceResult.getHitPosition().toLocation(world);
            return new CollisionResult(hitBlock, hitTarget, hitLocation);
        } else {
            RayTraceResult rayTraceResult = world.rayTraceBlocks(arrowLocation, arrowVelocity, length, FluidCollisionMode.NEVER);

            if (rayTraceResult == null || rayTraceResult.getHitBlock() != hitBlock) {
                // When the ray trace cannot find a hit location, take the center of the hit entity
                Location hitLocation = hitBlock.getLocation().add(BLOCK_CENTER_OFFSET, BLOCK_CENTER_OFFSET, BLOCK_CENTER_OFFSET);

                return new CollisionResult(hitBlock, null, hitLocation);
            }

            Location hitLocation = rayTraceResult.getHitPosition().toLocation(world);
            return new CollisionResult(hitBlock, null, hitLocation);
        }
    }

    private void scheduleSoundPlayTasks(List<GameSound> sounds, Supplier<Location> locationSupplier) {
        for (GameSound sound : sounds) {
            Schedule schedule = scheduler.createSingleRunSchedule(sound.getDelay());
            schedule.addTask(() -> audioEmitter.playSound(sound, locationSupplier.get()));
            schedule.start();

            soundPlaySchedules.add(schedule);
        }
    }
}
