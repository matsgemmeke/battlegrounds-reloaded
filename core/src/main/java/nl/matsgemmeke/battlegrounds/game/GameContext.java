package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.audio.AudioEmitter;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Acts as a facade for a {@link Game} for outside classes. Provides information about the game and handles logic.
 */
public interface GameContext extends AudioEmitter {

    /**
     * Looks for potential targets for a {@link GameEntity} around a specific {@link Location}.
     *
     * @param gameEntity the entity
     * @param location the location
     * @param range the range
     * @return all targets inside the range
     */
    @NotNull
    Collection<GameEntity> getTargets(@NotNull GameEntity gameEntity, @NotNull Location location, double range);

    /**
     * Checks if the given location produces a collision.
     *
     * @param location the location
     * @return whether a collision is produced
     */
    boolean producesCollisionAt(@NotNull Location location);

    /**
     * Registers an item entity to the game and creates a new {@link GameItem} instance.
     *
     * @param item the item
     * @return the created item instance
     */
    @NotNull
    GameItem registerItem(@NotNull Item item);
}
