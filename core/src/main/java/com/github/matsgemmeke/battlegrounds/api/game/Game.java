package com.github.matsgemmeke.battlegrounds.api.game;

import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an activity which groups multiple players together to play under the rules of the plugin.
 */
public interface Game {

    /**
     * Adds a player to the context and creates a {@link GamePlayer} instance.
     *
     * @param player the player to add
     * @return the created {@link GamePlayer} instance
     */
    @NotNull
    GamePlayer addPlayer(@NotNull Player player);

    /**
     * Finds the {@link GamePlayer} instance for a player entity. Returns null if there is no entry for the player.
     *
     * @param player the player entity
     * @return the {@link GamePlayer} entry or null if there is none for the player
     */
    @Nullable
    GamePlayer getGamePlayer(@NotNull Player player);

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
     * @param gamePlayer the player
     * @param event the event
     * @return whether the context has accepted the event
     */
    boolean onInteract(@NotNull GamePlayer gamePlayer, @NotNull PlayerInteractEvent event);

    /**
     * Executes logic that handles item drops by players.
     *
     * @param gamePlayer the player
     * @param event the event
     * @return whether the context has accepted the event
     */
    boolean onItemDrop(@NotNull GamePlayer gamePlayer, @NotNull PlayerDropItemEvent event);

    /**
     * Executes logic that handles switching of held items by players.
     *
     * @param gamePlayer the player
     * @param event the event
     * @return whether the context has accepted the event
     */
    boolean onItemHeld(@NotNull GamePlayer gamePlayer, @NotNull PlayerItemHeldEvent event);

    /**
     * Executes logic that handles item pickups by players.
     *
     * @param gamePlayer the player
     * @param event the event
     * @return whether the context has accepted the event
     */
    boolean onPickupItem(@NotNull GamePlayer gamePlayer, @NotNull EntityPickupItemEvent event);
}
