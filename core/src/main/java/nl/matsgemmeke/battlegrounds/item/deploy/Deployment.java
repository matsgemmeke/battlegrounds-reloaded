package nl.matsgemmeke.battlegrounds.item.deploy;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.damage.Damage;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.Activator;
import nl.matsgemmeke.battlegrounds.item.deploy.object.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.deploy.state.DeploymentState;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
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

/**
 * Represents a deployment process.
 */
public class Deployment {

    private final AudioEmitter audioEmitter;
    private final CollisionResultAdapter collisionResultAdapter;
    private final DeploymentProperties properties;
    private final ItemEffect itemEffect;
    private final ParticleEffectSpawner particleEffectSpawner;
    private final Scheduler scheduler;
    private final Set<TriggerExecutor> triggerExecutors;
    private final Set<TriggerRun> triggerRuns;
    private final String itemName;
    @Nullable
    private Activator activator;
    @Nullable
    private Actor currentActor;
    private boolean deployed;
    private boolean pending;
    @Nullable
    private DeploymentObject currentDeploymentObject;
    private DeploymentState state;

    @Inject
    public Deployment(
            AudioEmitter audioEmitter,
            CollisionResultAdapter collisionResultAdapter,
            ParticleEffectSpawner particleEffectSpawner,
            Scheduler scheduler,
            @Assisted String itemName,
            @Assisted DeploymentProperties properties,
            @Assisted DeploymentState state,
            @Assisted ItemEffect itemEffect
    ) {
        this.audioEmitter = audioEmitter;
        this.collisionResultAdapter = collisionResultAdapter;
        this.particleEffectSpawner = particleEffectSpawner;
        this.scheduler = scheduler;
        this.itemName = itemName;
        this.properties = properties;
        this.state = state;
        this.itemEffect = itemEffect;
        this.triggerExecutors = new HashSet<>();
        this.triggerRuns = new HashSet<>();
        this.deployed = false;
        this.pending = false;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public void activate(Deployer deployer) {
        audioEmitter.playSounds(properties.manualActivationSounds(), deployer.getDeployLocation());

        deployer.setHeldItem(null);

        Schedule delaySchedule = scheduler.createSingleRunSchedule(properties.manualActivationDelay());
        delaySchedule.addTask(() -> triggerRuns.forEach(TriggerRun::notifyObservers));
        delaySchedule.start();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    public void assignActivator(Activator activator) {
        this.activator = activator;
    }

    public void destroy(Damage damage) {
        if (currentActor == null || currentDeploymentObject == null) {
            throw new IllegalStateException("Illegal call to destroy deployment that is not deployed yet");
        }

        deployed = false;

        if (properties.activateEffectOnDestruction() && damage.type() != DamageType.ENVIRONMENTAL_DAMAGE) {
            itemEffect.activatePerformances();
        }

        if (properties.removeDeploymentOnDestruction()) {
            currentDeploymentObject.remove();
        }

        if (properties.undoEffectOnDestruction()) {
            itemEffect.rollbackPerformances();
        }

        ParticleEffect particleEffect = properties.destructionParticleEffect();

        if (particleEffect != null) {
            particleEffectSpawner.spawnParticleEffect(particleEffect, currentActor.getLocation());
        }
    }

    public void prepareActivator(Deployer deployer) {
        if (activator == null) {
            return;
        }

        activator.prepare(deployer);
    }

    public void processDeploymentResult(DeploymentResult result) {
        currentActor = result.actor();
        currentDeploymentObject = result.deploymentObject();

        state = state.processAction(this, result);
    }

    public void replaceActor(Actor actor) {
        currentActor = actor;

        triggerRuns.forEach(triggerRun -> triggerRun.replaceActor(actor));
        itemEffect.getLatestPerformance().ifPresent(latestPerformance -> latestPerformance.changeActor(actor));
    }

    public void reset() {
        if (currentDeploymentObject == null || !properties.removeDeploymentOnReset()) {
            return;
        }

        pending = false;
        currentDeploymentObject.remove();
    }

    public void scheduleDeploymentCooldown(Deployer deployer, long cooldown) {
        deployer.setCanDeploy(false);

        Schedule delaySchedule = scheduler.createSingleRunSchedule(cooldown);
        delaySchedule.addTask(() -> deployer.setCanDeploy(true));
        delaySchedule.start();
    }

    public void startTriggerExecutors(Deployer deployer, Actor actor) {
        UUID sourceId = deployer.getUniqueId();
        Location startingLocation = actor.getLocation();
        TriggerContext triggerContext = new TriggerContext(sourceId, actor);

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);
            triggerRun.addObserver(triggerResult -> this.activateEffect(triggerResult, deployer, currentActor, startingLocation));
            triggerRun.start();

            triggerRuns.add(triggerRun);
        }
    }

    private void activateEffect(TriggerResult triggerResult, DamageSource damageSource, Actor actor, Location startingLocation) {
        CollisionResult collisionResult = collisionResultAdapter.adapt(triggerResult);
        ItemEffectContext effectContext = new ItemEffectContext(itemName, collisionResult, damageSource, startingLocation, actor);

        itemEffect.startPerformance(effectContext);
    }
}
