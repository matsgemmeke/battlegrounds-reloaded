package nl.matsgemmeke.battlegrounds.item.deploy;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
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
    private final DeploymentProperties deploymentProperties;
    private final ItemEffect itemEffect;
    private final ParticleEffectSpawner particleEffectSpawner;
    private final Scheduler scheduler;
    private final Set<TriggerExecutor> triggerExecutors;
    private final Set<TriggerRun> triggerRuns;
    @Nullable
    private Activator activator;
    private boolean pending;
    private boolean performing;
    @Nullable
    private DeploymentObject deploymentObject;

    @Inject
    public DeploymentHandler(
            AudioEmitter audioEmitter,
            CollisionResultAdapter collisionResultAdapter,
            ParticleEffectSpawner particleEffectSpawner,
            Scheduler scheduler,
            @Assisted DeploymentProperties deploymentProperties,
            @Assisted ItemEffect itemEffect
    ) {
        this.audioEmitter = audioEmitter;
        this.collisionResultAdapter = collisionResultAdapter;
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
        return deploymentObject;
    }

    public void activateDeployment(Deployer deployer) {
        audioEmitter.playSounds(deploymentProperties.manualActivationSounds(), deployer.getDeployLocation());

        deployer.setHeldItem(null);

        Schedule delaySchedule = scheduler.createSingleRunSchedule(deploymentProperties.manualActivationDelay());
        delaySchedule.addTask(itemEffect::activatePerformances);
        delaySchedule.start();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    public void cleanupDeployment() {
        if (deploymentObject == null || !deploymentProperties.removeDeploymentOnCleanup()) {
            return;
        }

        performing = false;
        deploymentObject.remove();
    }

    public void destroyDeployment() {
        if (deploymentObject == null) {
            return;
        }

        performing = false;
        itemEffect.cancelPerformances();

        if (deploymentProperties.activateEffectOnDestruction()
                && (deploymentObject.getLastDamage() == null || deploymentObject.getLastDamage().type() != DamageType.ENVIRONMENTAL_DAMAGE)) {
            itemEffect.activatePerformances();
        }

        if (deploymentProperties.removeDeploymentOnDestruction()) {
            deploymentObject.remove();
        }

        if (deploymentProperties.undoEffectOnDestruction()) {
            itemEffect.rollbackPerformances();
        }

        ParticleEffect particleEffect = deploymentProperties.destructionParticleEffect();

        if (particleEffect != null) {
            particleEffectSpawner.spawnParticleEffect(particleEffect, deploymentObject.getLocation());
        }
    }

    public boolean isAwaitingDeployment() {
        return performing && deploymentObject != null && !deploymentObject.isPhysical();
    }

    public boolean isDeployed() {
        return performing && deploymentObject != null && deploymentObject.isPhysical();
    }

    public boolean isPerforming() {
        return performing;
    }

    public void processDeploymentResult(DeploymentResult deploymentResult) {
        performing = true;
        deploymentObject = deploymentResult.deploymentObject();

        if (pending) {
            Actor actor = deploymentResult.actor();

            itemEffect.getLatestPerformance().ifPresent(latestPerformance -> latestPerformance.changeActor(actor));
            triggerRuns.forEach(triggerRun -> triggerRun.replaceActor(actor));
            return;
        }

        DamageSource damageSource = deploymentResult.deployer();
        UUID sourceId = damageSource.getUniqueId();
        Actor actor = deploymentResult.actor();
        TriggerContext triggerContext = new TriggerContext(sourceId, actor);
        Location startingLocation = actor.getLocation();

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);
            triggerRun.addObserver(triggerResult -> this.activateEffect(triggerResult, damageSource, actor, startingLocation));
            triggerRun.start();

            if (!deploymentObject.isPhysical()) {
                triggerRuns.add(triggerRun);
            }
        }

        if (deploymentObject.isPhysical()) {
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
        ItemEffectContext effectContext = new ItemEffectContext(collisionResult, damageSource, actor, null, startingLocation);

        itemEffect.startPerformance(effectContext);
        triggerRuns.forEach(TriggerRun::cancel);
        triggerRuns.clear();
    }
}
