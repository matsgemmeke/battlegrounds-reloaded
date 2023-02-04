package com.github.matsgemmeke.battlegrounds.api.game;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Describes the circumstances that form the setting for events that happen in Battlegrounds.
 */
public interface BattleContext {

    /**
     * Adds a player to the context and creates a {@link BattlePlayer} instance.
     *
     * @param player the player to add
     * @return the created {@link BattlePlayer} instance
     */
    @NotNull
    BattlePlayer addPlayer(@NotNull Player player);

    /**
     * Finds the {@link BattlePlayer} instance for a player entity. Returns null if there is no entry for the player.
     *
     * @param player the player entity
     * @return the {@link BattlePlayer} entry or null if there is none for the player
     */
    @Nullable
    BattlePlayer getBattlePlayer(@NotNull Player player);

    /**
     * Looks for potential targets for a {@link BattleEntity} around a specific {@link Location}.
     *
     * @param battleEntity the entity
     * @param location the location
     * @param range the range
     * @return all targets inside the range
     */
    @NotNull
    Iterable<BattleEntity> getTargets(@NotNull BattleEntity battleEntity, @NotNull Location location, double range);

    /**
     * Gets whether a player is present in the context.
     *
     * @param player the player
     * @return whether the context has the player
     */
    boolean hasPlayer(@NotNull Player player);

    /**
     * Executes logic that handles item interactions by players.
     *
     * @param battlePlayer the player
     * @param event the interaction event
     * @return whether the context has accepted the interaction
     */
    boolean onInteract(@NotNull BattlePlayer battlePlayer, @NotNull PlayerInteractEvent event);

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
     * @param location the location
     * @return whether a collision is produced
     */
    boolean producesCollisionAt(@NotNull Location location);
}
