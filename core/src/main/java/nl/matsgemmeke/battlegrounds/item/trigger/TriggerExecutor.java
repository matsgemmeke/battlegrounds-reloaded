package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;

import java.util.function.Supplier;

public class TriggerExecutor {

    private final Supplier<Schedule> scheduleSupplier;
    private final Trigger trigger;

    public TriggerExecutor(Trigger trigger, Supplier<Schedule> scheduleSupplier) {
        this.trigger = trigger;
        this.scheduleSupplier = scheduleSupplier;
    }

    public TriggerRun createTriggerRun(TriggerContext context) {
        Schedule schedule = scheduleSupplier.get();
        return new TriggerRun(schedule, trigger, context);
    }
}
