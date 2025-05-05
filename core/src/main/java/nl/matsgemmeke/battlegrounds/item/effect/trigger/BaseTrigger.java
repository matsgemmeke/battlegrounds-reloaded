package nl.matsgemmeke.battlegrounds.item.effect.trigger;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTrigger implements Trigger {

    @NotNull
    private final List<TriggerObserver> observers;

    public BaseTrigger() {
        this.observers = new ArrayList<>();
    }

    public void addObserver(@NotNull TriggerObserver observer) {
        observers.add(observer);
    }

    protected void notifyObservers() {
        observers.forEach(TriggerObserver::onActivate);
    }
}
