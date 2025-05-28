package nl.matsgemmeke.battlegrounds.item.trigger.floor;

import nl.matsgemmeke.battlegrounds.item.trigger.BaseTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class FloorHitTrigger extends BaseTrigger {

    private static final double Y_SUBTRACTION = 0.01;

    @NotNull
    private final Schedule schedule;
    private boolean started;

    public FloorHitTrigger(@NotNull Schedule schedule) {
        this.schedule = schedule;
        this.started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public void start(@NotNull TriggerContext context) {
        schedule.addTask(() -> this.runCheck(context));
        schedule.start();
        started = true;
    }

    private void runCheck(@NotNull TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            this.stop();
            return;
        }

        // Subtract a minimal amount from the y coordinate to make the sure we get the block right below the object
        Block blockBelowObject = target.getLocation().subtract(0, Y_SUBTRACTION, 0).getBlock();

        if (blockBelowObject.isPassable()) {
            return;
        }

        this.notifyObservers();
    }

    public void stop() {
        schedule.stop();
        started = false;
    }
}
