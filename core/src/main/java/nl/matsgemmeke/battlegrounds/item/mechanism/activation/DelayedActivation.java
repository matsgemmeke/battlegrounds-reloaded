package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Activation that initiates the mechanism after a specific delay.
 */
public class DelayedActivation extends BaseItemMechanismActivation {

    private long delayUntilActivation;
    @NotNull
    private TaskRunner taskRunner;

    public DelayedActivation(@NotNull ItemMechanism mechanism, @NotNull TaskRunner taskRunner, long delayUntilActivation) {
        super(mechanism);
        this.taskRunner = taskRunner;
        this.delayUntilActivation = delayUntilActivation;
    }

    public void prime(@NotNull ItemHolder holder, @Nullable Deployable object) {
        deployedObjects.add(object);

        BukkitTask task = taskRunner.runTaskLater(() -> this.handleActivation(holder, deployedObjects.get(0)), delayUntilActivation);

        tasks.add(task);
    }
}
