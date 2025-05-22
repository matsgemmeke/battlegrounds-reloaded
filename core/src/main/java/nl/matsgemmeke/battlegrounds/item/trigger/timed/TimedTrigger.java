package nl.matsgemmeke.battlegrounds.item.trigger.timed;

import nl.matsgemmeke.battlegrounds.item.trigger.BaseTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

public class TimedTrigger extends BaseTrigger {

    @NotNull
    private final Schedule schedule;
    private boolean activated;

    public TimedTrigger(@NotNull Schedule schedule) {
        this.schedule = schedule;
        this.activated = false;
    }

    public boolean isActivated() {
        return activated;
    }

    public void activate(@NotNull TriggerContext context) {
        if (activated) {
            return;
        }

        schedule.addTask(() -> {
            this.notifyObservers();
            this.deactivate();
        });
        schedule.start();
        activated = true;
    }

    public void deactivate() {
        schedule.stop();
        activated = false;
    }
}
