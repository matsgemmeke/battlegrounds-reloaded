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
import org.bukkit.entity.Entity;

public class DeploymentHandler {

    private final AudioEmitter audioEmitter;
    private final DeploymentProperties deploymentProperties;
    private final ItemEffect itemEffect;
    private final ParticleEffectSpawner particleEffectSpawner;
    private final Scheduler scheduler;
    private Activator activator;
    private boolean deployed;
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
        this.deployed = false;
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

        deployed = false;
        deploymentObject.remove();
    }

    public void destroyDeployment() {
        if (deploymentObject == null) {
            return;
        }

        deployed = false;
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

    public void handleDeployment(Deployment deployment, Deployer deployer, Entity deployerEntity) {
        DeploymentResult result = deployment.perform(deployer, deployerEntity);
        deploymentObject = result.object();

        if (!result.success()) {
            return;
        }

        ItemEffectPerformance latestPerformance = itemEffect.getLatestPerformance().orElse(null);

        if (latestPerformance != null && !latestPerformance.isReleased()) {
            latestPerformance.changeSource(deploymentObject);
        } else {
            Location initiationLocation = deployer.getDeployLocation();
            ItemEffectContext context = new ItemEffectContext(deployerEntity, deploymentObject, initiationLocation);

            itemEffect.startPerformance(context);
        }

        if (activator != null) {
            activator.prepare(deployer);
        }

        if (deploymentObject.isDeployed()) {
            deployed = true;
            deployer.setCanDeploy(false);

            Schedule delaySchedule = scheduler.createSingleRunSchedule(deploymentObject.getCooldown());
            delaySchedule.addTask(() -> deployer.setCanDeploy(true));
            delaySchedule.start();
        }
    }

    public boolean isAwaitingDeployment() {
        return deploymentObject != null && !deploymentObject.isDeployed();
    }

    public boolean isDeployed() {
        return deployed;
    }
}
