package com.github.matsgemmeke.battlegrounds.item.mechanism;

public interface ReloadSystem {

    /**
     * Activates a reload operation.
     *
     * @return whether the reload operation was successful
     */
    boolean activate();

    /**
     * Cancels the current reload operation.
     */
    void cancel();
}
