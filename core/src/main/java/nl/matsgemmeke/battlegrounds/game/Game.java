package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointContainer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an activity which groups multiple players together to play under the rules of the plugin.
 */
public interface Game {

    /**
     * Gets the entity container which keeps {@link GamePlayer} instances.
     *
     * @return the player container
     */
    @NotNull
    EntityContainer<GamePlayer> getPlayerContainer();

    /**
     * Gets the spawn point container.
     *
     * @return the spawn point container of the game
     */
    @NotNull
    SpawnPointContainer getSpawnPointContainer();
}
