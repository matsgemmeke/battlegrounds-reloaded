package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TriggerExecutor {

    @NotNull
    private final Supplier<Schedule> scheduleSupplier;
    @NotNull
    private final TriggerNew trigger;

    public TriggerExecutor(@NotNull TriggerNew trigger, @NotNull Supplier<Schedule> scheduleSupplier) {
        this.trigger = trigger;
        this.scheduleSupplier = scheduleSupplier;
    }

    public TriggerRun createTriggerRun(TriggerContext context) {
        Schedule schedule = scheduleSupplier.get();
        return new TriggerRun(schedule, trigger, context);
    }
}
