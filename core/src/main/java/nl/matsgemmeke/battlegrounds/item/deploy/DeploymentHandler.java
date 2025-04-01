package nl.matsgemmeke.battlegrounds.item.deploy;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeploymentHandler {

    @NotNull
    private final AudioEmitter audioEmitter;
    private boolean deployed;
    @Nullable
    private DeploymentObject deploymentObject;
    @NotNull
    private final DeploymentProperties deploymentProperties;
    @NotNull
    private final ItemEffect effect;
    @NotNull
    private final ParticleEffectSpawner particleEffectSpawner;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public DeploymentHandler(
            @NotNull ParticleEffectSpawner particleEffectSpawner,
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull DeploymentProperties deploymentProperties,
            @Assisted @NotNull AudioEmitter audioEmitter,
            @Assisted @NotNull ItemEffect effect
    ) {
        this.particleEffectSpawner = particleEffectSpawner;
        this.taskRunner = taskRunner;
        this.deploymentProperties = deploymentProperties;
        this.audioEmitter = audioEmitter;
        this.effect = effect;
        this.deployed = false;
    }

    @Nullable
    public DeploymentObject getDeploymentObject() {
        return deploymentObject;
    }

    public void activateDeployment(@NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        audioEmitter.playSounds(deploymentProperties.activationSounds(), deployerEntity.getLocation());

        deployer.setHeldItem(null);

        taskRunner.runTaskLater(effect::activateInstantly, deploymentProperties.activationDelay());
    }

    public void destroyDeployment() {
        if (deploymentObject == null) {
            return;
        }

        effect.cancelActivation();

        if (deploymentProperties.activateEffectOnDestroy()
                && (deploymentObject.getLastDamage() == null || deploymentObject.getLastDamage().type() != DamageType.ENVIRONMENTAL_DAMAGE)) {
            effect.activateInstantly();
        }

        if (deploymentProperties.removeOnDestroy()) {
            deploymentObject.remove();
        }

        if (deploymentProperties.resetEffectOnDestroy()) {
            effect.reset();
        }

        ParticleEffect particleEffect = deploymentProperties.destroyParticleEffect();

        if (particleEffect != null) {
            particleEffectSpawner.spawnParticleEffect(particleEffect, deploymentObject.getWorld(), deploymentObject.getLocation());
        }
    }

    public void handleDeployment(@NotNull Deployment deployment, @NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        DeploymentResult result = deployment.perform(deployer, deployerEntity);
        deploymentObject = result.object();

        if (!result.success()) {
            return;
        }

        if (!effect.isPrimed()) {
            effect.prime(new ItemEffectContext(deployer, deployerEntity, deploymentObject));
        } else {
            effect.deploy(deploymentObject);
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
