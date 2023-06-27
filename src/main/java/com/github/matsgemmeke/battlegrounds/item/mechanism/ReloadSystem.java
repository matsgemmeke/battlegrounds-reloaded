package com.github.matsgemmeke.battlegrounds.item.mechanism;

public interface ReloadSystem {

    /**
     * Gets whether the reload system operation is being cancelled.
     *
     * @return whether the reload is being cancelled
     */
    boolean isCancelled();

    /**
     * Sets whether the reload system operation is being cancelled.
     *
     * @param cancelled whether the reload is being cancelled
     */
    void setCancelled(boolean cancelled);

    /**
     * Activates a reload operation.
     */
    void activate();
}
