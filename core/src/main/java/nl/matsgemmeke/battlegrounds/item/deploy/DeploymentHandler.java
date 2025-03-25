package nl.matsgemmeke.battlegrounds.item.deploy;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeploymentHandler {

    @NotNull
    private final ActivationProperties activationProperties;
    @NotNull
    private final AudioEmitter audioEmitter;
    private boolean deployed;
    @Nullable
    private DeploymentObject object;
    @NotNull
    private final ItemEffect effect;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public DeploymentHandler(
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull ActivationProperties activationProperties,
            @Assisted @NotNull AudioEmitter audioEmitter,
            @Assisted @NotNull ItemEffect effect
    ) {
        this.taskRunner = taskRunner;
        this.activationProperties = activationProperties;
        this.audioEmitter = audioEmitter;
        this.effect = effect;
        this.deployed = false;
    }

    public void activateDeployment(@NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        audioEmitter.playSounds(activationProperties.activationSounds(), deployerEntity.getLocation());

        deployer.setHeldItem(null);

        taskRunner.runTaskLater(effect::activateInstantly, activationProperties.activationDelay());
    }

    public void handleDeployment(@NotNull Deployment deployment, @NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        DeploymentResult result = deployment.perform(deployer, deployerEntity);
        object = result.object();

        if (!result.success()) {
            return;
        }

        if (effect.isPrimed()) {
            effect.deploy(object);
        } else {
            ItemEffectContext effectContext = new ItemEffectContext(deployer, deployerEntity, object);

            effect.prime(effectContext);
        }

        deployed = true;
        deployer.setCanDeploy(false);

        taskRunner.runTaskLater(() -> deployer.setCanDeploy(true), object.getCooldown());
    }

    public boolean isAwaitingDeployment() {
        return object != null && !object.isDeployed();
    }

    public boolean isDeployed() {
        return deployed;
    }
}
