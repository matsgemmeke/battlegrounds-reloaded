package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TriggerSession {

    @NotNull
    private final Schedule schedule;
    @NotNull
    private final Set<TriggerObserver> observers;

    public TriggerSession(@NotNull Schedule schedule) {
        this.schedule = schedule;
        this.observers = new HashSet<>();
    }

    public void addObserver(TriggerObserver observer) {
        observers.add(observer);
    }

    public void cancel() {
        schedule.stop();
    }

    public void notifyObservers() {
        observers.forEach(TriggerObserver::onActivate);
    }

    public void start() {
        schedule.start();
    }
}
