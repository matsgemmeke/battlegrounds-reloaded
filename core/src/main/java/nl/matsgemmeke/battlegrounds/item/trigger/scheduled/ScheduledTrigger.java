package nl.matsgemmeke.battlegrounds.item.trigger.scheduled;

import nl.matsgemmeke.battlegrounds.item.trigger.BaseTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

public class ScheduledTrigger extends BaseTrigger {

    private final boolean continuous;
    @NotNull
    private final Schedule schedule;
    private boolean started;

    public ScheduledTrigger(@NotNull Schedule schedule, boolean continuous) {
        this.schedule = schedule;
        this.continuous = continuous;
        this.started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public void start(@NotNull TriggerContext context) {
        if (started) {
            return;
        }

        schedule.addTask(this::activate);
        schedule.start();
        started = true;
    }

    private void activate() {
        this.notifyObservers();

        if (!continuous) {
            this.stop();
        }
    }

    public void stop() {
        schedule.stop();
        started = false;
    }
}
