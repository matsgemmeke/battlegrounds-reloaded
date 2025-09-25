package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TriggerRun {

    @NotNull
    private final Schedule schedule;
    @NotNull
    private final Set<TriggerObserver> observers;
    @NotNull
    private final TriggerNew trigger;
    @NotNull
    private final TriggerContext context;
    private boolean started;

    public TriggerRun(@NotNull Schedule schedule, @NotNull TriggerNew trigger, @NotNull TriggerContext context) {
        this.schedule = schedule;
        this.trigger = trigger;
        this.context = context;
        this.observers = new HashSet<>();
        this.started = false;
    }

    public void addObserver(TriggerObserver observer) {
        observers.add(observer);
    }

    public void cancel() {
        if (!started) {
            return;
        }

        schedule.stop();
        schedule.clearTasks();
        started = false;
    }

    public void start() {
        if (started) {
            return;
        }

        schedule.addTask(this::performTriggerCheck);
        schedule.start();
        started = true;
    }

    private void performTriggerCheck() {
        if (!context.target().exists()) {
            this.cancel();
            return;
        }

        if (!trigger.activates(context)) {
            return;
        }

        observers.forEach(TriggerObserver::onActivate);
    }
}
