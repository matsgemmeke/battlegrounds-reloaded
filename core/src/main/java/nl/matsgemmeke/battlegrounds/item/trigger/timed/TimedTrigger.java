package nl.matsgemmeke.battlegrounds.item.trigger.timed;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.trigger.BaseTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimedTrigger extends BaseTrigger {

    private boolean activated;
    @Nullable
    private BukkitTask currentTask;
    private final long delayUntilActivation;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public TimedTrigger(@NotNull TaskRunner taskRunner, @Assisted long delayUntilActivation) {
        this.taskRunner = taskRunner;
        this.delayUntilActivation = delayUntilActivation;
        this.activated = false;
    }

    public boolean isActivated() {
        return activated;
    }

    public void activate(@NotNull TriggerContext context) {
        if (activated) {
            return;
        }

        activated = true;
        currentTask = taskRunner.runTaskLater(this::notifyObservers, delayUntilActivation);
    }

    public void deactivate() {
        if (!activated || currentTask == null) {
            return;
        }

        activated = false;
        currentTask.cancel();
    }
}
