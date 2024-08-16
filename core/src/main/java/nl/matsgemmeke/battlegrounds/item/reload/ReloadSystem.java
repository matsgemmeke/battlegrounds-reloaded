package nl.matsgemmeke.battlegrounds.item.reload;

import org.jetbrains.annotations.NotNull;

public interface ReloadSystem {

    /**
     * Attempts to cancel the current reload operation.
     *
     * @return whether the reload was cancelled
     */
    boolean cancelReload();

    /**
     * Gets whether the reload system is performing a reload.
     *
     * @return whether a reload is being performed
     */
    boolean isPerforming();

    /**
     * Performs a reload operation.
     *
     * @param performer the entity who performs the reload
     * @return whether an operation is performed
     */
    boolean performReload(@NotNull ReloadPerformer performer);
}
