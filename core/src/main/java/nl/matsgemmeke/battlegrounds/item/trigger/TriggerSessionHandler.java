package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TriggerSessionHandler {

    @NotNull
    private final Supplier<Schedule> scheduleSupplier;
    @NotNull
    private final TriggerNew trigger;

    public TriggerSessionHandler(@NotNull TriggerNew trigger, @NotNull Supplier<Schedule> scheduleSupplier) {
        this.trigger = trigger;
        this.scheduleSupplier = scheduleSupplier;
    }

    public TriggerSession createSession(TriggerContext context) {
        Schedule schedule = scheduleSupplier.get();
        TriggerSession session = new TriggerSession(schedule);

        schedule.addTask(() -> this.performTriggerCheck(session, context));

        return session;
    }

    private void performTriggerCheck(TriggerSession session, TriggerContext context) {
        if (!trigger.activates(context)) {
            return;
        }

        session.notifyObservers();
    }
}
