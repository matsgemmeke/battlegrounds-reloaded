package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.StaticItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.StaticTriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class FireballLauncher implements ProjectileLauncher {

    private static final long SCHEDULE_DELAY = 0L;
    private static final long SCHEDULE_INTERVAL = 1L;

    private final AudioEmitter audioEmitter;
    private final FireballProperties properties;
    private final ItemEffect itemEffect;
    private final ParticleEffectSpawner particleEffectSpawner;
    private final ProjectileHitActionRegistry projectileHitActionRegistry;
    private final Scheduler scheduler;
    private final Set<Schedule> soundPlaySchedules;

    @Inject
    public FireballLauncher(
            AudioEmitter audioEmitter,
            ParticleEffectSpawner particleEffectSpawner,
            ProjectileHitActionRegistry projectileHitActionRegistry,
            Scheduler scheduler,
            @Assisted FireballProperties properties,
            @Assisted ItemEffect itemEffect
    ) {
        this.itemEffect = itemEffect;
        this.audioEmitter = audioEmitter;
        this.particleEffectSpawner = particleEffectSpawner;
        this.projectileHitActionRegistry = projectileHitActionRegistry;
        this.scheduler = scheduler;
        this.properties = properties;
        this.soundPlaySchedules = new HashSet<>();
    }

    @Override
    public void cancel() {
        soundPlaySchedules.forEach(Schedule::stop);
    }

    @Override
    public void launch(LaunchContext context) {
        DamageSource damageSource = context.damageSource();
        Location initiationLocation = context.direction();
        Vector velocity = context.direction().getDirection().multiply(properties.velocity());
        ProjectileLaunchSource projectileSource = context.projectileSource();
        Supplier<Location> soundLocationSupplier = context.soundLocationSupplier();

        Fireball fireball = projectileSource.launchProjectile(SmallFireball.class, velocity);
        fireball.setIsIncendiary(false);
        fireball.setYield(0.0f);

        Schedule schedule = scheduler.createRepeatingSchedule(SCHEDULE_DELAY, SCHEDULE_INTERVAL);
        schedule.addTask(() -> this.displayParticleEffect(fireball));
        schedule.start();

        projectileHitActionRegistry.registerProjectileHitAction(fireball, () -> this.onHit(damageSource, initiationLocation, fireball, schedule));

        this.scheduleSoundPlayTasks(properties.shotSounds(), soundLocationSupplier);
    }

    private void displayParticleEffect(Fireball fireball) {
        Location location = fireball.getLocation();

        properties.getTrajectoryParticleEffect().ifPresent(particleEffect -> {
            particleEffectSpawner.spawnParticleEffect(particleEffect, location);
        });
    }

    private void onHit(DamageSource damageSource, Location initiationLocation, Fireball fireball, Schedule schedule) {
        Location fireballLocation = fireball.getLocation();
        World fireballWorld = fireball.getWorld();

        StaticItemEffectSource effectSource = new StaticItemEffectSource(fireballLocation, fireballWorld);
        StaticTriggerTarget triggerTarget = new StaticTriggerTarget(fireballLocation, fireballWorld);

        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, initiationLocation);

        itemEffect.startPerformance(context);

        schedule.stop();

        projectileHitActionRegistry.removeProjectileHitAction(fireball);
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
