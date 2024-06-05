package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.entity.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.ItemMechanism;
import org.jetbrains.annotations.NotNull;

public interface ReloadSystem extends ItemMechanism {

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
