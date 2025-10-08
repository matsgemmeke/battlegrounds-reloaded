package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectNew;
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

public class FireballLauncher implements ProjectileLauncher {

    private static final long SCHEDULE_DELAY = 0L;
    private static final long SCHEDULE_INTERVAL = 1L;

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final FireballProperties properties;
    @NotNull
    private final ItemEffectNew itemEffect;
    @NotNull
    private final ParticleEffectSpawner particleEffectSpawner;
    @NotNull
    private final ProjectileHitActionRegistry projectileHitActionRegistry;
    @NotNull
    private final Scheduler scheduler;

    @Inject
    public FireballLauncher(
            @NotNull AudioEmitter audioEmitter,
            @NotNull ParticleEffectSpawner particleEffectSpawner,
            @NotNull ProjectileHitActionRegistry projectileHitActionRegistry,
            @NotNull Scheduler scheduler,
            @Assisted @NotNull FireballProperties properties,
            @Assisted @NotNull ItemEffectNew itemEffect
    ) {
        this.itemEffect = itemEffect;
        this.audioEmitter = audioEmitter;
        this.particleEffectSpawner = particleEffectSpawner;
        this.projectileHitActionRegistry = projectileHitActionRegistry;
        this.scheduler = scheduler;
        this.properties = properties;
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
        schedule.start();

        projectileHitActionRegistry.registerProjectileHitAction(fireball, () -> this.onHit(entity, initiationLocation, fireball, schedule));
    }

    private void displayParticleEffect(Fireball fireball) {
        Location location = fireball.getLocation();

        properties.getTrajectoryParticleEffect().ifPresent(particleEffect -> {
            particleEffectSpawner.spawnParticleEffect(particleEffect, location);
        });
    }

    private void onHit(Entity entity, Location initiationLocation, Fireball fireball, Schedule schedule) {
        Location fireballLocation = fireball.getLocation();
        World fireballWorld = fireballLocation.getWorld();

        StaticSource source = new StaticSource(fireballLocation, fireballWorld);
        ItemEffectContext context = new ItemEffectContext(entity, source, initiationLocation);

        itemEffect.startPerformance(context);

        schedule.stop();

        projectileHitActionRegistry.removeProjectileHitAction(fireball);
    }
}
