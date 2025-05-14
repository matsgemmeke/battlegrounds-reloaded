package nl.matsgemmeke.battlegrounds.item.effect.trigger;

import org.jetbrains.annotations.NotNull;

public interface Trigger {

    /**
     * Activates the trigger, so it start monitoring the given {@link TriggerContext}.
     *
     * @param context the trigger context
     */
    void activate(@NotNull TriggerContext context);

    /**
     * Adds an observer to the trigger that gets notified when the trigger executes.
     *
     * @param observer the observer function
     */
    void addObserver(@NotNull TriggerObserver observer);

    /**
     * Deactivates the trigger and cancels the monitoring.
     */
    void deactivate();

    /**
     * Gets whether the trigger system is currently activated and monitoring.
     *
     * @return whether the trigger is activated
     */
    boolean isActivated();
}
