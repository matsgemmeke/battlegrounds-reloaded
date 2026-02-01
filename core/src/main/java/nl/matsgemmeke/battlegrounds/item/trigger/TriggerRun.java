package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;

import java.util.HashSet;
import java.util.Set;

public class TriggerRun {

    private final Schedule schedule;
    private final Set<TriggerObserver> observers;
    private final Trigger trigger;
    private boolean repeating;
    private boolean started;
    private TriggerContext context;

    public TriggerRun(Schedule schedule, Trigger trigger, TriggerContext context) {
        this.schedule = schedule;
        this.trigger = trigger;
        this.context = context;
        this.observers = new HashSet<>();
        this.repeating = false;
        this.started = false;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void addObserver(TriggerObserver observer) {
        observers.add(observer);
    }

    public void replaceActor(Actor actor) {
        context = context.withActor(actor);
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
        TriggerResult result = trigger.check(context);

        if (!result.activates()) {
            return;
        }

        observers.forEach(observer -> observer.onActivate(result));

        if (!repeating) {
            schedule.stop();
        }
    }
}
