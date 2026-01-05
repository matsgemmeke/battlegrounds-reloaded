package nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileRegistry;
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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ArrowLauncher implements ProjectileLauncher {

    private final ArrowProperties properties;
    private final AudioEmitter audioEmitter;
    private final ItemEffect itemEffect;
    private final ProjectileHitActionRegistry projectileHitActionRegistry;
    private final ProjectileRegistry projectileRegistry;
    private final Scheduler scheduler;
    private final Set<Schedule> soundPlaySchedules;

    @Inject
    public ArrowLauncher(
            AudioEmitter audioEmitter,
            ProjectileHitActionRegistry projectileHitActionRegistry,
            ProjectileRegistry projectileRegistry,
            Scheduler scheduler,
            @Assisted ArrowProperties properties,
            @Assisted ItemEffect itemEffect
    ) {
        this.audioEmitter = audioEmitter;
        this.projectileHitActionRegistry = projectileHitActionRegistry;
        this.projectileRegistry = projectileRegistry;
        this.scheduler = scheduler;
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
        DamageSource damageSource = context.damageSource();
        Location initiationLocation = context.direction();
        Vector velocity = context.direction().getDirection().multiply(properties.velocity());
        ProjectileLaunchSource projectileSource = context.projectileSource();
        Supplier<Location> soundLocationSupplier = context.soundLocationSupplier();

        Arrow arrow = projectileSource.launchProjectile(Arrow.class, velocity);
        arrow.setPickupStatus(PickupStatus.DISALLOWED);
        arrow.setVelocity(velocity);

        projectileRegistry.register(arrow.getUniqueId());
        projectileHitActionRegistry.registerProjectileHitAction(arrow, hitLocation -> this.onHit(damageSource, arrow, initiationLocation, hitLocation));

        this.scheduleSoundPlayTasks(properties.shotSounds(), soundLocationSupplier);
    }

    private void onHit(DamageSource damageSource, Arrow arrow, Location initiationLocation, Location hitLocation) {
        World world = arrow.getWorld();

        StaticItemEffectSource effectSource = new StaticItemEffectSource(hitLocation, world);
        StaticTriggerTarget triggerTarget = new StaticTriggerTarget(hitLocation, world);

        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, initiationLocation);

        itemEffect.startPerformance(context);

        projectileHitActionRegistry.removeProjectileHitAction(arrow);
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
