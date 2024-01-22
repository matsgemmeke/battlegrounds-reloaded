package com.github.matsgemmeke.battlegrounds.api.game;

import com.github.matsgemmeke.battlegrounds.api.entity.GameEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Acts as a facade for a {@link Game} for outside classes. Provides information about the game and handles logic.
 */
public interface GameContext {

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
     * Plays a {@link GameSound} for all players in the context.
     *
     * @param sound the sound
     * @param location the location to play the sound
     */
    void playSound(@NotNull GameSound sound, @NotNull Location location);

    /**
     * Plays multiple {@link GameSound}s for all players in the context.
     *
     * @param sound the sound
     * @param location the location to play the sound
     */
    void playSounds(@NotNull Iterable<GameSound> sound,@NotNull Location location);

    /**
     * Checks if the given location produces a collision with a block.
     *
     * @param block the block to check the collision with
     * @param location the location
     * @return whether a collision is produced
     */
    boolean producesCollisionAt(@NotNull Block block, @NotNull Location location);
}
