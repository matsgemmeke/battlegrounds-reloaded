package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.item.holder.ItemHolder;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A mechanism component of an item. Produces a certain effect when activated.
 */
public interface ItemMechanism {

    /**
     * Activates the mechanism.
     *
     * @param droppedItem the dropped item entity
     * @param holder the entity who activated the mechanism
     */
    void activate(@Nullable Item droppedItem, @NotNull ItemHolder holder);
}
