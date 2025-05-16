package nl.matsgemmeke.battlegrounds.item.trigger.floor;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.trigger.BaseTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class FloorHitTrigger extends BaseTrigger {

    private static final double Y_SUBTRACTION = 0.01;
    private static final long RUNNABLE_DELAY = 0L;

    private boolean activated;
    private BukkitTask task;
    private final long periodBetweenChecks;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public FloorHitTrigger(@NotNull TaskRunner taskRunner, @Assisted long periodBetweenChecks) {
        this.taskRunner = taskRunner;
        this.periodBetweenChecks = periodBetweenChecks;
        this.activated = false;
    }

    public boolean isActivated() {
        return activated;
    }

    public void activate(@NotNull TriggerContext context) {
        activated = true;
        task = taskRunner.runTaskTimer(() -> this.runCheck(context), RUNNABLE_DELAY, periodBetweenChecks);
    }

    private void runCheck(@NotNull TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            task.cancel();
            return;
        }

        // Subtract a minimal amount from the y coordinate to make the sure we get the block right below the object
        Block blockBelowObject = target.getLocation().subtract(0, Y_SUBTRACTION, 0).getBlock();

        if (blockBelowObject.isPassable()) {
            return;
        }

        this.notifyObservers();
        task.cancel();
    }

    public void deactivate() {
        if (task == null) {
            return;
        }

        activated = false;
        task.cancel();
    }
}
