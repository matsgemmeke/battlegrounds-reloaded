package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

public interface GameItem extends GameEntity {

    /**
     * Gets the {@link Item} entity of the object.
     *
     * @return the dropped item entity
     */
    @NotNull
    Item getEntity();
}
