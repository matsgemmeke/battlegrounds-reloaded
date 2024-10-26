package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Activation that initiates the mechanism after a specific delay.
 */
public class DelayedActivation extends BaseItemMechanismActivation {

    private long delayUntilActivation;
    @NotNull
    private TaskRunner taskRunner;

    public DelayedActivation(@NotNull ItemEffect effect, @NotNull TaskRunner taskRunner, long delayUntilActivation) {
        super(effect);
        this.taskRunner = taskRunner;
        this.delayUntilActivation = delayUntilActivation;
    }

    public void primeDeployedObject(@NotNull ItemHolder holder, @NotNull Deployable object) {
        deployedObjects.add(object);

        BukkitTask task = taskRunner.runTaskLater(() -> effect.activate(holder, object), delayUntilActivation);

        tasks.add(task);
    }

    public void primeInHand(@NotNull ItemHolder holder, @NotNull ItemStack itemStack) {
        deployedObjects.add(null);

        BukkitTask task = taskRunner.runTaskLater(() -> this.executeDeferredDeploymentActivation(holder, itemStack), delayUntilActivation);

        tasks.add(task);
    }

    private void executeDeferredDeploymentActivation(@NotNull ItemHolder holder, @NotNull ItemStack itemStack) {
        Deployable oldestObject = deployedObjects.get(0);

        if (oldestObject != null) {
            effect.activate(holder, oldestObject);
        } else {
            effect.activate(holder, itemStack);
        }
    }
}
