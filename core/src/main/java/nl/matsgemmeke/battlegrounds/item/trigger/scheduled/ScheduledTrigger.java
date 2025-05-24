package nl.matsgemmeke.battlegrounds.item.trigger.scheduled;

import nl.matsgemmeke.battlegrounds.item.trigger.BaseTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

public class ScheduledTrigger extends BaseTrigger {

    private final boolean continuous;
    @NotNull
    private final Schedule schedule;
    private boolean activated;

    public ScheduledTrigger(@NotNull Schedule schedule, boolean continuous) {
        this.schedule = schedule;
        this.continuous = continuous;
        this.activated = false;
    }

    public boolean isActivated() {
        return activated;
    }

    public void activate(@NotNull TriggerContext context) {
        if (activated) {
            return;
        }

        schedule.addTask(this::execute);
        schedule.start();
        activated = true;
    }

    private void execute() {
        this.notifyObservers();

        if (!continuous) {
            this.deactivate();
        }
    }

    public void deactivate() {
        schedule.stop();
        activated = false;
    }
}
