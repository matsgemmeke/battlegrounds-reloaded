package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.util.Procedure;
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
     * Performs a reload operation. Includes a callback function that is called each time a reload operation was
     * finished. This callback function can be called multiple times.
     *
     * @param performer the entity who performs the reload
     * @param onReload the callback function for when a reload has been performed
     * @return whether an operation is performed
     */
    boolean performReload(@NotNull ReloadPerformer performer, @NotNull Procedure onReload);
}
