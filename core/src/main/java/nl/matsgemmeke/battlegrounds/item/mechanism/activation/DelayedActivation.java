package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.Droppable;
import nl.matsgemmeke.battlegrounds.item.holder.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Activation that triggers the mechanism after a specific delay.
 */
public class DelayedActivation implements ItemMechanismActivation {

    private boolean primed;
    @Nullable
    private BukkitTask currentTask;
    @NotNull
    private Droppable item;
    @NotNull
    private ItemMechanism mechanism;
    private long delayUntilActivation;
    @NotNull
    private TaskRunner taskRunner;

    public DelayedActivation(
            @NotNull Droppable item,
            @NotNull ItemMechanism mechanism,
            @NotNull TaskRunner taskRunner,
            long delayUntilActivation
    ) {
        this.item = item;
        this.mechanism = mechanism;
        this.taskRunner = taskRunner;
        this.delayUntilActivation = delayUntilActivation;
        this.primed = false;
    }

    public boolean isPrimed() {
        return primed;
    }

    public void activate(@NotNull ItemHolder holder) {
        if (currentTask != null) {
            currentTask.cancel();
        }

        mechanism.activate(item.getDroppedItem(), holder);
    }

    public void prime(@NotNull ItemHolder holder) {
        primed = true;

        currentTask = taskRunner.runTaskLater(() -> this.activate(holder), delayUntilActivation);
    }
}
