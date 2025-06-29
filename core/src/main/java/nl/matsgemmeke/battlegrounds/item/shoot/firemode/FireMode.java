package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import org.jetbrains.annotations.NotNull;

public interface FireMode {

    /**
     * Registers a {@link ShotObserver} to be notified whenever a shot is fired as part of this {@code FireMode}'s
     * firing cycle.
     * <p>
     * Observers are typically used to perform actions such as spawning projectiles, playing effects, or applying
     * recoil when a shot occurs. Multiple observers may be registered and will be notified in the order they were
     * added.
     *
     * @param observer the observer to register
     */
    void addShotObserver(@NotNull ShotObserver observer);

    /**
     * Starts a shooting cycle.
     *
     * @return whether the cycle was started
     */
    boolean startCycle();

    /**
     * Attempts to cancel the current shooting cycle.
     *
     * @return whether the cycle was cancelled
     */
    boolean cancelCycle();

    /**
     * Gets the rate of fire of the firemode in rounds per minute.
     *
     * @return the firemode's rate of fire
     */
    int getRateOfFire();

    /**
     * Gets whether the fire mode is performing a cycle.
     *
     * @return whether the fire mode is cycling
     */
    boolean isCycling();
}
