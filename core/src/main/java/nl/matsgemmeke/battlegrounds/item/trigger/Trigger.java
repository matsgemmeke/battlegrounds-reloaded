package nl.matsgemmeke.battlegrounds.item.trigger;

import org.jetbrains.annotations.NotNull;

public interface Trigger {

    /**
     * Adds an observer to the trigger that gets notified when the trigger activates.
     *
     * @param observer the observer function
     */
    void addObserver(@NotNull TriggerObserver observer);

    /**
     * Gets whether the trigger system is currently started and monitoring.
     *
     * @return whether the trigger is started
     */
    boolean isStarted();

    /**
     * Starts the trigger by monitoring the given {@link TriggerContext}.
     *
     * @param context the trigger context
     */
    void start(@NotNull TriggerContext context);

    /**
     * Stops the trigger and cancels the monitoring.
     */
    void stop();
}
