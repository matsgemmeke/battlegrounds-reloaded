package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;

import java.util.HashSet;
import java.util.Set;

public class TriggerRun {

    private final Schedule schedule;
    private final Set<TriggerObserver> observers;
    private final Trigger trigger;
    private final TriggerContext context;
    private boolean started;

    public TriggerRun(Schedule schedule, Trigger trigger, TriggerContext context) {
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
        if (!trigger.activates(context)) {
            return;
        }

        observers.forEach(TriggerObserver::onActivate);
    }
}
