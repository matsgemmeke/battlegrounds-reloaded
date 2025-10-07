package nl.matsgemmeke.battlegrounds.item.deploy;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.*;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeploymentHandler {

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final DeploymentProperties deploymentProperties;
    @NotNull
    private final ItemEffectNew itemEffect;
    @NotNull
    private final ParticleEffectSpawner particleEffectSpawner;
    @NotNull
    private final TaskRunner taskRunner;
    @Nullable
    private Activator activator;
    private boolean deployed;
    @Nullable
    private DeploymentObject deploymentObject;

    @Inject
    public DeploymentHandler(
            @NotNull AudioEmitter audioEmitter,
            @NotNull ParticleEffectSpawner particleEffectSpawner,
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull DeploymentProperties deploymentProperties,
            @Assisted @NotNull ItemEffectNew itemEffect
    ) {
        this.audioEmitter = audioEmitter;
        this.particleEffectSpawner = particleEffectSpawner;
        this.taskRunner = taskRunner;
        this.deploymentProperties = deploymentProperties;
        this.itemEffect = itemEffect;
        this.deployed = false;
    }

    @Nullable
    public Activator getActivator() {
        return activator;
    }

    public void setActivator(@Nullable Activator activator) {
        this.activator = activator;
    }

    @Nullable
    public DeploymentObject getDeploymentObject() {
        return deploymentObject;
    }

    public void activateDeployment(@NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        audioEmitter.playSounds(deploymentProperties.manualActivationSounds(), deployerEntity.getLocation());

        deployer.setHeldItem(null);

        taskRunner.runTaskLater(itemEffect::activatePerformances, deploymentProperties.manualActivationDelay());
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

    public void handleDeployment(@NotNull Deployment deployment, @NotNull Deployer deployer, @NotNull Entity deployerEntity) {
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

            taskRunner.runTaskLater(() -> deployer.setCanDeploy(true), deploymentObject.getCooldown());
        }
    }

    public boolean isAwaitingDeployment() {
        return deploymentObject != null && !deploymentObject.isDeployed();
    }

    public boolean isDeployed() {
        return deployed;
    }
}
