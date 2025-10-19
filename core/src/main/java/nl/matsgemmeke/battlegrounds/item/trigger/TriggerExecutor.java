package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;

import java.util.function.Supplier;

public class TriggerExecutor {

    private final Supplier<Schedule> scheduleSupplier;
    private final Trigger trigger;
    private boolean repeating;

    public TriggerExecutor(Trigger trigger, Supplier<Schedule> scheduleSupplier) {
        this.trigger = trigger;
        this.scheduleSupplier = scheduleSupplier;
        this.repeating = true;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public TriggerRun createTriggerRun(TriggerContext context) {
        Schedule schedule = scheduleSupplier.get();

        TriggerRun triggerRun = new TriggerRun(schedule, trigger, context);
        triggerRun.setRepeating(repeating);
        return triggerRun;
    }
}
