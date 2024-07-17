package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Acts as a facade for a {@link Game} for outside classes. Provides information about the game and handles logic.
 */
public interface GameContext extends AudioEmitter {

    /**
     * Registers an item entity to the game and creates a new {@link GameItem} instance.
     *
     * @param item the item
     * @return the created item instance
     */
    @NotNull
    GameItem registerItem(@NotNull Item item);
}
