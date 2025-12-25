package nl.matsgemmeke.battlegrounds.item.deploy;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.*;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class DeploymentHandler {

    private final AudioEmitter audioEmitter;
    private final DeploymentProperties deploymentProperties;
    private final ItemEffect itemEffect;
    private final ParticleEffectSpawner particleEffectSpawner;
    private final Scheduler scheduler;
    @Nullable
    private Activator activator;
    private boolean performing;
    @Nullable
    private DeploymentObject deploymentObject;

    @Inject
    public DeploymentHandler(
            AudioEmitter audioEmitter,
            ParticleEffectSpawner particleEffectSpawner,
            Scheduler scheduler,
            @Assisted DeploymentProperties deploymentProperties,
            @Assisted ItemEffect itemEffect
    ) {
        this.audioEmitter = audioEmitter;
        this.particleEffectSpawner = particleEffectSpawner;
        this.scheduler = scheduler;
        this.deploymentProperties = deploymentProperties;
        this.itemEffect = itemEffect;
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

        ItemEffectPerformance latestPerformance = itemEffect.getLatestPerformance().orElse(null);

        if (latestPerformance != null) {
            latestPerformance.changeEffectSource(deploymentObject);
            return;
        }

        if (!deploymentObject.isPhysical()) {
            this.processPendingDeployment(deploymentResult);
        } else {
            this.processCompletedDeployment(deploymentResult);
        }
    }

    private void processPendingDeployment(DeploymentResult deploymentResult) {
        Deployer deployer = deploymentResult.deployer();
        Location initiationLocation = deploymentResult.deploymentObject().getLocation();
        ItemEffectContext context = new ItemEffectContext(deployer, deploymentObject, initiationLocation);

        itemEffect.startPerformance(context);
    }

    private void processCompletedDeployment(DeploymentResult deploymentResult) {
        Deployer deployer = deploymentResult.deployer();
        Location initiationLocation = deployer.getDeployLocation();
        long cooldown = deploymentResult.cooldown();

        ItemEffectContext context = new ItemEffectContext(deployer, deploymentObject, initiationLocation);

        itemEffect.startPerformance(context);

        deployer.setCanDeploy(false);

        Schedule delaySchedule = scheduler.createSingleRunSchedule(cooldown);
        delaySchedule.addTask(() -> deployer.setCanDeploy(true));
        delaySchedule.start();

        if (activator != null) {
            activator.prepare(deployer);
        }
    }
}
