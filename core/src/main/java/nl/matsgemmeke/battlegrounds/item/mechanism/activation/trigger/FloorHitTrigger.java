package nl.matsgemmeke.battlegrounds.item.mechanism.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FloorHitTrigger implements Trigger {

    private static final double Y_SUBTRACTION = 0.01;
    private static final long RUNNABLE_DELAY = 0L;

    @Nullable
    private BukkitTask task;
    @NotNull
    private List<TriggerListener> listeners;
    private long periodBetweenChecks;
    @NotNull
    private TaskRunner taskRunner;

    public FloorHitTrigger(@NotNull TaskRunner taskRunner, long periodBetweenChecks) {
        this.taskRunner = taskRunner;
        this.periodBetweenChecks = periodBetweenChecks;
        this.listeners = new ArrayList<>();
    }

    public void addListener(@NotNull TriggerListener listener) {
        listeners.add(listener);
    }

    public void checkTriggerActivation(@NotNull ItemHolder holder, @NotNull Deployable object) {
        task = taskRunner.runTaskTimer(() -> {
            // Subtract a minimal amount from the y coordinate to make the sure we get the block right below the object
            Block blockBelowObject = object.getLocation().subtract(0, Y_SUBTRACTION, 0).getBlock();

            if (blockBelowObject.isPassable()) {
                return;
            }

            this.notifyListeners(holder, object);
            task.cancel();
        }, RUNNABLE_DELAY, periodBetweenChecks);
    }

    private void notifyListeners(@NotNull ItemHolder holder, @NotNull Deployable object) {
        for (TriggerListener listener : listeners) {
            listener.onTrigger(holder, object);
        }
    }
}
