package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitResult;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileRegistry;
import nl.matsgemmeke.battlegrounds.item.actor.StaticActor;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.sound.api.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.api.SoundPlayback;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FireballLauncher implements ProjectileLauncher {

    private static final long SCHEDULE_DELAY = 0L;
    private static final long SCHEDULE_INTERVAL = 1L;

    private final CollisionResultAdapter collisionResultAdapter;
    private final FireballProperties properties;
    private final ItemEffect itemEffect;
    private final ParticleEffectSpawner particleEffectSpawner;
    private final ProjectileHitActionRegistry projectileHitActionRegistry;
    private final ProjectileRegistry projectileRegistry;
    private final Scheduler scheduler;
    private final Set<SoundPlayback> soundPlaybacks;
    private final SoundEmitter soundEmitter;

    @Inject
    public FireballLauncher(
            CollisionResultAdapter collisionResultAdapter,
            ParticleEffectSpawner particleEffectSpawner,
            ProjectileHitActionRegistry projectileHitActionRegistry,
            ProjectileRegistry projectileRegistry,
            Scheduler scheduler,
            SoundEmitter soundEmitter,
            @Assisted FireballProperties properties,
            @Assisted ItemEffect itemEffect
    ) {
        this.collisionResultAdapter = collisionResultAdapter;
        this.particleEffectSpawner = particleEffectSpawner;
        this.projectileHitActionRegistry = projectileHitActionRegistry;
        this.projectileRegistry = projectileRegistry;
        this.scheduler = scheduler;
        this.soundEmitter = soundEmitter;
        this.properties = properties;
        this.itemEffect = itemEffect;
        this.soundPlaybacks = new HashSet<>();
    }

    @Override
    public void cancel() {
        soundPlaybacks.forEach(SoundPlayback::cancel);
        soundPlaybacks.clear();
    }

    @Override
    public void launch(LaunchContext context) {
        String itemName = context.itemName();
        DamageSource damageSource = context.damageSource();
        Location startingLocation = context.direction();
        Vector velocity = context.direction().getDirection().multiply(properties.velocity());
        ProjectileLaunchSource projectileSource = context.projectileSource();

        SoundTemplate launchSoundTemplate = properties.launchSoundTemplate();

        if (launchSoundTemplate != null) {
            SoundPlayback soundPlayback = soundEmitter.playSoundTemplate(launchSoundTemplate, context.soundLocationSupplier());

            soundPlaybacks.add(soundPlayback);
        }

        Fireball fireball = projectileSource.launchProjectile(SmallFireball.class, velocity);
        fireball.setIsIncendiary(false);
        fireball.setYield(0.0f);

        Schedule schedule = scheduler.createRepeatingSchedule(SCHEDULE_DELAY, SCHEDULE_INTERVAL);
        schedule.addTask(() -> this.displayParticleEffect(fireball));
        schedule.start();

        projectileRegistry.register(fireball.getUniqueId());
        projectileHitActionRegistry.registerProjectileHitAction(fireball, projectileHitResult -> this.onHit(projectileHitResult, itemName, damageSource, startingLocation, fireball, schedule));
    }

    private void displayParticleEffect(Fireball fireball) {
        Location location = fireball.getLocation();

        Optional.ofNullable(properties.trajectoryParticleEffect()).ifPresent(particleEffect -> {
            particleEffectSpawner.spawnParticleEffect(particleEffect, location);
        });
    }

    private void onHit(ProjectileHitResult projectileHitResult, String itemName, DamageSource damageSource, Location startingLocation, Fireball fireball, Schedule schedule) {
        CollisionResult collisionResult = collisionResultAdapter.adapt(projectileHitResult, fireball);
        Location fireballLocation = fireball.getLocation();
        World fireballWorld = fireball.getWorld();

        StaticActor actor = new StaticActor(fireballLocation, fireballWorld);
        ItemEffectContext context = new ItemEffectContext(itemName, collisionResult, damageSource, startingLocation, actor);

        itemEffect.startPerformance(context);

        schedule.stop();

        projectileHitActionRegistry.removeProjectileHitAction(fireball);
    }
}
