package nl.matsgemmeke.battlegrounds.item.deploy;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeploymentHandler {

    private boolean performing;
    @Nullable
    private DeploymentObject object;
    @NotNull
    private final ItemEffect effect;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public DeploymentHandler(@NotNull TaskRunner taskRunner, @Assisted @NotNull ItemEffect effect) {
        this.taskRunner = taskRunner;
        this.effect = effect;
    }

    public boolean isAwaitingDeployment() {
        return object != null && !object.isDeployed();
    }

    public boolean isDeployed() {
        return object != null;
    }

    public boolean isPerforming() {
        return performing;
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

        performing = true;

        taskRunner.runTaskLater(() -> performing = false, object.getCooldown());
    }
}
