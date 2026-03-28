package nl.matsgemmeke.battlegrounds.item.deploy;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.*;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultAdapter;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DeploymentHandler {

    private final AudioEmitter audioEmitter;
    private final CollisionResultAdapter collisionResultAdapter;
    private final DeploymentObjectRegistry deploymentObjectRegistry;
    private final DeploymentProperties deploymentProperties;
    private final ItemEffect itemEffect;
    private final ParticleEffectSpawner particleEffectSpawner;
    private final Scheduler scheduler;
    private final Set<TriggerExecutor> triggerExecutors;
    private final Set<TriggerRun> triggerRuns;
    @Nullable
    private Activator activator;
    @Nullable
    private Actor currentActor;
    private boolean pending;
    private boolean performing;
    @Nullable
    private DeploymentObject currentDeploymentObject;

    @Inject
    public DeploymentHandler(
            AudioEmitter audioEmitter,
            CollisionResultAdapter collisionResultAdapter,
            DeploymentObjectRegistry deploymentObjectRegistry,
            ParticleEffectSpawner particleEffectSpawner,
            Scheduler scheduler,
            @Assisted DeploymentProperties deploymentProperties,
            @Assisted ItemEffect itemEffect
    ) {
        this.audioEmitter = audioEmitter;
        this.collisionResultAdapter = collisionResultAdapter;
        this.deploymentObjectRegistry = deploymentObjectRegistry;
        this.particleEffectSpawner = particleEffectSpawner;
        this.scheduler = scheduler;
        this.deploymentProperties = deploymentProperties;
        this.itemEffect = itemEffect;
        this.triggerExecutors = new HashSet<>();
        this.triggerRuns = new HashSet<>();
        this.pending = false;
        this.performing = false;
    }

    public Activator getActivator() {
        return activator;
    }

    public void setActivator(Activator activator) {
        this.activator = activator;
    }

    public DeploymentObject getDeploymentObject() {
        return currentDeploymentObject;
    }

    public void activateDeployment(Deployer deployer) {
        audioEmitter.playSounds(deploymentProperties.manualActivationSounds(), deployer.getDeployLocation());

        deployer.setHeldItem(null);

        Schedule delaySchedule = scheduler.createSingleRunSchedule(deploymentProperties.manualActivationDelay());
        delaySchedule.addTask(() -> triggerRuns.forEach(TriggerRun::notifyObservers));
        delaySchedule.start();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    public void cleanupDeployment() {
        if (currentDeploymentObject == null || !deploymentProperties.removeDeploymentOnCleanup()) {
            return;
        }

        performing = false;
        currentDeploymentObject.remove();
    }

    public void destroyDeployment() {
        if (currentDeploymentObject == null) {
            return;
        }

        performing = false;
        boolean lastDamagedByEnvironmentalDamage = currentDeploymentObject.getLastDamage().map(damage -> damage.type() == DamageType.ENVIRONMENTAL_DAMAGE).orElse(false);

        if (deploymentProperties.activateEffectOnDestruction() && !lastDamagedByEnvironmentalDamage) {
            itemEffect.activatePerformances();
        }

        if (deploymentProperties.removeDeploymentOnDestruction()) {
            currentDeploymentObject.remove();
        }

        if (deploymentProperties.undoEffectOnDestruction()) {
            itemEffect.rollbackPerformances();
        }

        ParticleEffect particleEffect = deploymentProperties.destructionParticleEffect();

        if (particleEffect != null) {
            particleEffectSpawner.spawnParticleEffect(particleEffect, currentDeploymentObject.getLocation());
        }
    }

    public boolean isAwaitingDeployment() {
        return performing && currentDeploymentObject != null && !currentDeploymentObject.isPhysical();
    }

    public boolean isDeployed() {
        return performing && currentDeploymentObject != null && currentDeploymentObject.isPhysical();
    }

    public boolean isPerforming() {
        return performing;
    }

    public void processDeploymentResult(DeploymentResult deploymentResult) {
        performing = true;

        if (pending) {
            deploymentObjectRegistry.unregister(currentDeploymentObject);
            deploymentObjectRegistry.register(deploymentResult.deploymentObject());

            currentActor = deploymentResult.actor();
            currentDeploymentObject = deploymentResult.deploymentObject();

            itemEffect.getLatestPerformance().ifPresent(latestPerformance -> latestPerformance.changeActor(currentActor));
            triggerRuns.forEach(triggerRun -> triggerRun.replaceActor(currentActor));
            return;
        }

        currentActor = deploymentResult.actor();
        currentDeploymentObject = deploymentResult.deploymentObject();

        deploymentObjectRegistry.register(currentDeploymentObject);

        DamageSource damageSource = deploymentResult.deployer();
        UUID sourceId = damageSource.getUniqueId();
        TriggerContext triggerContext = new TriggerContext(sourceId, currentActor);
        Location startingLocation = currentActor.getLocation();

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);
            triggerRun.addObserver(triggerResult -> this.activateEffect(triggerResult, damageSource, currentActor, startingLocation));
            triggerRun.start();

            triggerRuns.add(triggerRun);
        }

        if (currentDeploymentObject.isPhysical()) {
            pending = false;

            Deployer deployer = deploymentResult.deployer();
            long cooldown = deploymentResult.cooldown();

            deployer.setCanDeploy(false);

            Schedule delaySchedule = scheduler.createSingleRunSchedule(cooldown);
            delaySchedule.addTask(() -> deployer.setCanDeploy(true));
            delaySchedule.start();

            if (activator != null) {
                activator.prepare(deployer);
            }
        } else {
            pending = true;
        }
    }

    private void activateEffect(TriggerResult triggerResult, DamageSource damageSource, Actor actor, Location startingLocation) {
        CollisionResult collisionResult = collisionResultAdapter.adapt(triggerResult);
        ItemEffectContext effectContext = new ItemEffectContext(collisionResult, damageSource, actor, startingLocation);

        itemEffect.startPerformance(effectContext);
    }
}
