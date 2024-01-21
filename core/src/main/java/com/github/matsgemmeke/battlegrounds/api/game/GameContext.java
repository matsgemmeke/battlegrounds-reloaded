package com.github.matsgemmeke.battlegrounds.api.game;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Acts as a facade for a {@link Game} for outside classes. Provides information about the game and handles logic.
 */
public interface GameContext {

    /**
     * Looks for potential targets for a {@link BattleEntity} around a specific {@link Location}.
     *
     * @param battleEntity the entity
     * @param location the location
     * @param range the range
     * @return all targets inside the range
     */
    @NotNull
    Collection<BattleEntity> getTargets(@NotNull BattleEntity battleEntity, @NotNull Location location, double range);

    /**
     * Plays a {@link BattleSound} for all players in the context.
     *
     * @param sound the sound
     * @param location the location to play the sound
     */
    void playSound(@NotNull BattleSound sound, @NotNull Location location);

    /**
     * Plays multiple {@link BattleSound}s for all players in the context.
     *
     * @param sound the sound
     * @param location the location to play the sound
     */
    void playSounds(@NotNull Iterable<BattleSound> sound,@NotNull Location location);

    /**
     * Checks if the given location produces a collision with a block.
     *
     * @param block the block to check the collision with
     * @param location the location
     * @return whether a collision is produced
     */
    boolean producesCollisionAt(@NotNull Block block, @NotNull Location location);
}
