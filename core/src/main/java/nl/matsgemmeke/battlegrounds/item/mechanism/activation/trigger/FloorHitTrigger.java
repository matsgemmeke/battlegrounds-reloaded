package nl.matsgemmeke.battlegrounds.item.mechanism.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FloorHitTrigger implements Trigger {

    private static final double Y_SUBTRACTION = 0.01;
    private static final long RUNNABLE_DELAY = 0L;

    private BukkitTask task;
    @NotNull
    private List<TriggerObserver> observers;
    private long periodBetweenChecks;
    @NotNull
    private TaskRunner taskRunner;

    public FloorHitTrigger(@NotNull TaskRunner taskRunner, long periodBetweenChecks) {
        this.taskRunner = taskRunner;
        this.periodBetweenChecks = periodBetweenChecks;
        this.observers = new ArrayList<>();
    }

    public void addObserver(@NotNull TriggerObserver observer) {
        observers.add(observer);
    }

    public void checkTriggerActivation(@NotNull ItemHolder holder, @NotNull Deployable object) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(holder, object), RUNNABLE_DELAY, periodBetweenChecks);
    }

    private void runCheck(@NotNull ItemHolder holder, @NotNull Deployable object) {
        if (!object.exists()) {
            task.cancel();
            return;
        }

        // Subtract a minimal amount from the y coordinate to make the sure we get the block right below the object
        Block blockBelowObject = object.getLocation().subtract(0, Y_SUBTRACTION, 0).getBlock();

        if (blockBelowObject.isPassable()) {
            return;
        }

        this.notifyObservers(holder, object);
        task.cancel();
    }

    private void notifyObservers(@NotNull ItemHolder holder, @NotNull Deployable object) {
        for (TriggerObserver observer : observers) {
            observer.onTrigger(holder, object);
        }
    }
}
