package com.github.matsgemmeke.battlegrounds.api;

import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Stores and provides {@link BattleContext} instances to outside classes.
 */
public interface BattleContextProvider {

    /**
     * Adds a {@link FreemodeContext} instance to the provider.
     *
     * @param context the context to be added
     * @return whether the context was added
     */
    boolean addFreemodeContext(@NotNull FreemodeContext context);

    /**
     * Adds a {@link GameContext} instance to the provider.
     *
     * @param gameContext the context to be added
     * @return whether the context was added
     */
    boolean addGameContext(@NotNull GameContext gameContext);

    /**
     * Gets the {@link BattleContext} a player is currently in. Returns null if the player is not in any of the
     * registered contexts.
     *
     * @param player the player
     * @return the context the player is currently or null if the player is not present in any
     */
    @Nullable
    BattleContext getContext(Player player);

    /**
     * Gathers all context instances and returns them as an immutable collection. This collection should not be used to
     * add and remove instances from the manager. Use the {@code add} and {@code remove} methods instead for these
     * tasks.
     *
     * @return an immutable collection of context instances
     */
    @NotNull
    Collection<BattleContext> getContexts();

    /**
     * Gets a {@link GameContext} instance with a specific id from the provider. Returns null if the provider does not contain
     * an instance with the id.
     *
     * @param id the game id
     * @return the game instance or null if the provider does not have an instance with the id
     */
    @Nullable
    GameContext getGameContext(int id);

    /**
     * Removes a {@link FreemodeContext} instance from the provider.
     *
     * @param context the context to be removed
     * @return whether the context was removed
     */
    boolean removeFreemodeContext(@NotNull FreemodeContext context);

    /**
     * Removes a {@link GameContext} instance to the provider.
     *
     * @param context the context to be removed
     * @return whether the context was removed
     */
    boolean removeGameContext(@NotNull GameContext context);
}
