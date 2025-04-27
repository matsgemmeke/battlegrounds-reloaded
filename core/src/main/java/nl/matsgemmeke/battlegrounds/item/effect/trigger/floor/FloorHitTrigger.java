package nl.matsgemmeke.battlegrounds.item.effect.trigger.floor;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.BaseTrigger;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class FloorHitTrigger extends BaseTrigger {

    private static final double Y_SUBTRACTION = 0.01;
    private static final long RUNNABLE_DELAY = 0L;

    private boolean primed;
    private BukkitTask task;
    private final long periodBetweenChecks;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public FloorHitTrigger(@NotNull TaskRunner taskRunner, @Assisted long periodBetweenChecks) {
        this.taskRunner = taskRunner;
        this.periodBetweenChecks = periodBetweenChecks;
        this.primed = false;
    }

    public boolean isPrimed() {
        return primed;
    }

    public void cancel() {
        if (task == null) {
            return;
        }

        primed = false;
        task.cancel();
    }

    public void prime(@NotNull ItemEffectContext context) {
        primed = true;
        task = taskRunner.runTaskTimer(() -> this.runCheck(context), RUNNABLE_DELAY, periodBetweenChecks);
    }

    private void runCheck(@NotNull ItemEffectContext context) {
        ItemEffectSource source = context.getSource();

        if (!source.exists()) {
            task.cancel();
            return;
        }

        // Subtract a minimal amount from the y coordinate to make the sure we get the block right below the object
        Block blockBelowObject = source.getLocation().subtract(0, Y_SUBTRACTION, 0).getBlock();

        if (blockBelowObject.isPassable()) {
            return;
        }

        this.notifyObservers();
        task.cancel();
    }
}
