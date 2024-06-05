package nl.matsgemmeke.battlegrounds;

import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.game.training.TrainingMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Stores and provides {@link Game} instances to outside classes.
 */
public interface GameProvider {

    /**
     * Adds a {@link Session} instance to the provider.
     *
     * @param session the session to be added
     * @return whether the session was added
     */
    boolean addSession(@NotNull Session session);

    /**
     * Adds a {@link TrainingMode} instance to the provider. There should only be one instance assigned.
     *
     * @param trainingMode the instance to be assigned
     * @return whether the instance was assigned
     */
    boolean assignTrainingMode(@NotNull TrainingMode trainingMode);

    /**
     * Gets the {@link Game} a player is currently in. Returns null if the player is not in any of the
     * registered contexts.
     *
     * @param player the player
     * @return the context the player is currently or null if the player is not present in any
     */
    @Nullable
    Game getGame(@NotNull Player player);

    /**
     * Gathers all game instances and returns them as an immutable collection. This collection should not be used to
     * add and remove instances from the manager. Use the {@code add} and {@code remove} methods instead for these
     * tasks.
     *
     * @return an immutable collection of game instances
     */
    @NotNull
    Collection<Game> getGames();

    /**
     * Gets a {@link Session} instance with a specific id from the provider. Returns null if the provider does not
     * contain an instance with the id.
     *
     * @param id the session id
     * @return the session instance or null if the provider does not have an instance with the id
     */
    @Nullable
    Session getSession(int id);

    /**
     * Removes a {@link Session} instance to the provider.
     *
     * @param session the session to be removed
     * @return whether the session was removed
     */
    boolean removeSession(@NotNull Session session);
}
