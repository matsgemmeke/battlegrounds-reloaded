package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
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

    public void checkTriggerActivation(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(holder, source), RUNNABLE_DELAY, periodBetweenChecks);
    }

    private void runCheck(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        if (!source.exists()) {
            task.cancel();
            return;
        }

        // Subtract a minimal amount from the y coordinate to make the sure we get the block right below the object
        Block blockBelowObject = source.getLocation().subtract(0, Y_SUBTRACTION, 0).getBlock();

        if (blockBelowObject.isPassable()) {
            return;
        }

        this.notifyObservers(holder, source);
        task.cancel();
    }

    private void notifyObservers(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        for (TriggerObserver observer : observers) {
            observer.onTrigger(holder, source);
        }
    }
}
