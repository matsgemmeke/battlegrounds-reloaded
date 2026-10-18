package nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitResult;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileRegistry;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.ProjectileActor;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.sound.api.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.api.SoundPlayback;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;
import org.bukkit.*;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class ArrowLauncher implements ProjectileLauncher {

    private final ArrowProperties properties;
    private final CollisionResultAdapter collisionResultAdapter;
    private final ItemEffect itemEffect;
    private final ProjectileHitActionRegistry projectileHitActionRegistry;
    private final ProjectileRegistry projectileRegistry;
    private final Set<SoundPlayback> soundPlaybacks;
    private final Set<TriggerExecutor> triggerExecutors;
    private final SoundEmitter soundEmitter;

    @Inject
    public ArrowLauncher(
            CollisionResultAdapter collisionResultAdapter,
            ProjectileHitActionRegistry projectileHitActionRegistry,
            ProjectileRegistry projectileRegistry,
            SoundEmitter soundEmitter,
            @Assisted ArrowProperties properties,
            @Assisted ItemEffect itemEffect
    ) {
        this.collisionResultAdapter = collisionResultAdapter;
        this.projectileHitActionRegistry = projectileHitActionRegistry;
        this.projectileRegistry = projectileRegistry;
        this.soundEmitter = soundEmitter;
        this.properties = properties;
        this.itemEffect = itemEffect;
        this.soundPlaybacks = new HashSet<>();
        this.triggerExecutors = new HashSet<>();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
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
        Supplier<Location> soundLocationSupplier = context.soundLocationSupplier();

        SoundSequence soundSequence = properties.soundSequence();

        if (soundSequence != null) {
            SoundPlayback soundPlayback = soundEmitter.playSoundSequence(soundSequence, soundLocationSupplier);

            soundPlaybacks.add(soundPlayback);
        }

        Arrow arrow = projectileSource.launchProjectile(Arrow.class, velocity);
        arrow.setDamage(0.0);
        arrow.setPickupStatus(PickupStatus.DISALLOWED);
        arrow.setVelocity(velocity);

        UUID sourceId = damageSource.getUniqueId();
        ProjectileActor actor = new ProjectileActor(arrow);
        TriggerContext triggerContext = new TriggerContext(sourceId, actor);

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);
            triggerRun.addObserver(triggerResult -> this.processTriggerResult(triggerResult, arrow, itemName, actor, damageSource, startingLocation));
            triggerRun.start();
        }

        projectileRegistry.register(arrow.getUniqueId());
        projectileHitActionRegistry.registerProjectileHitAction(arrow, projectileHitResult -> this.processProjectileHit(projectileHitResult, arrow, itemName, actor, damageSource, startingLocation));
    }

    private void processTriggerResult(TriggerResult triggerResult, Arrow arrow, String itemName, Actor actor, DamageSource damageSource, Location startingLocation) {
        CollisionResult collisionResult = collisionResultAdapter.adapt(triggerResult);
        ItemEffectContext context = new ItemEffectContext(itemName, collisionResult, damageSource, startingLocation, actor);

        itemEffect.startPerformance(context);
        projectileRegistry.unregister(arrow.getUniqueId());
        projectileHitActionRegistry.removeProjectileHitAction(arrow);

        if (collisionResult.getHitTarget().isPresent()) {
            arrow.remove();
        }
    }

    private void processProjectileHit(ProjectileHitResult projectileHitResult, Arrow arrow, String itemName, Actor actor, DamageSource damageSource, Location startingLocation) {
        CollisionResult collisionResult = collisionResultAdapter.adapt(projectileHitResult, arrow);
        ItemEffectContext context = new ItemEffectContext(itemName, collisionResult, damageSource, startingLocation, actor);

        itemEffect.startPerformance(context);
        projectileRegistry.unregister(arrow.getUniqueId());
        projectileHitActionRegistry.removeProjectileHitAction(arrow);

        if (collisionResult.getHitTarget().isPresent()) {
            arrow.remove();
        }
    }
}
