package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.game.freeplay.Freeplay;

import java.util.*;

/**
 * Stores and provides {@link Game} context to client classes.
 */
public class GameContextProvider {

    private final Map<GameKey, Game> games;
    private final Map<GameKey, GameContext> gameContexts;
    private final Map<UUID, GameKey> entityGameRegistries;

    public GameContextProvider() {
        this.games = new HashMap<>();
        this.gameContexts = new HashMap<>();
        this.entityGameRegistries = new HashMap<>();
    }

    /**
     * Adds a game context to the provider.
     *
     * @param gameKey the game key
     * @param gameContext the game context instance
     */
    public void addGameContext(GameKey gameKey, GameContext gameContext) {
        gameContexts.put(gameKey, gameContext);
    }

    /**
     * Adds an arena instance to the provider.
     *
     * @param gameKey the game key
     * @param arena   the arena
     * @return        whether the arena was added
     */
    public boolean addArena(GameKey gameKey, Arena arena) {
        games.put(gameKey, arena);
        return true;
    }

    /**
     * Assigns the freeplay mode instance to the provider. This will only assign the freeplay mode once, as there
     * should only be one instance. Returns {@code true} if the instance was assigned, and {@code false} if there
     * already is an assigned instance.
     *
     * @param freeplay the freeplay mode instance
     * @return         whether the instance was assigned
     */
    public boolean assignFreeplay(Freeplay freeplay) {
        GameKey gameKey = GameKey.ofFreeplay();
        boolean containsFreeplay = games.keySet().stream().anyMatch(k -> k.equals(gameKey));

        if (containsFreeplay) {
            return false;
        }

        games.put(gameKey, freeplay);
        return true;
    }

    /**
     * Gets the game context by their game key. The return optional is empty when none of the registered game context
     * has the given game key.
     *
     * @param gameKey the game key
     * @return an optional which contains the corresponding game context or empty when none were found
     */
    public Optional<GameContext> getGameContext(GameKey gameKey) {
        for (GameKey otherKey : gameContexts.keySet()) {
            if (gameKey.equals(otherKey)) {
                return Optional.of(gameContexts.get(otherKey));
            }
        }

        return Optional.empty();
    }

    /**
     * Gets the game context under which a given entity is registered. Returns an empty optional when the given entity
     * id is not registered for any game context.
     *
     * @param entityId the entity id
     * @return         an optional with the game key
     */
    public Optional<GameContext> getGameContext(UUID entityId) {
        GameKey gameKey = entityGameRegistries.get(entityId);

        if (gameKey == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(gameContexts.get(gameKey));
    }

    public void registerEntity(UUID uniqueId, GameKey gameKey) {
        entityGameRegistries.put(uniqueId, gameKey);
    }

    public void unregisterEntity(UUID uniqueId) {
        entityGameRegistries.remove(uniqueId);
    }

    /**
     * Removes an arena instance from the provider.
     *
     * @param id the arena id
     * @return   whether the arena was removed
     */
    public boolean removeArena(int id) {
        GameKey gameKey = GameKey.ofArena(id);
        Optional<GameKey> arenaGameKey = games.keySet().stream().filter(k -> k.equals(gameKey)).findFirst();

        return arenaGameKey.filter(key -> games.remove(key) != null).isPresent();
    }

    /**
     * Gets whether an arena instance exists by matching an id. Returns {@code true} if an arena by the given id exists,
     * and {@code false} if not.
     *
     * @param id the arena id
     * @return   whether an arena by the given id exists
     */
    public boolean arenaExists(int id) {
        GameKey arenaGameKey = GameKey.ofArena(id);

        return games.keySet().stream().anyMatch(gameKey -> gameKey.equals(arenaGameKey));
    }
}
