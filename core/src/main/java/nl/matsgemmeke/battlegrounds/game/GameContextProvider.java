package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.game.openmode.OpenMode;
import nl.matsgemmeke.battlegrounds.game.session.Arena;

import java.util.*;

/**
 * Stores and provides {@link Game} context to client classes.
 */
public class GameContextProvider {

    private final Map<GameKey, Game> games;
    private final Map<GameKey, GameContext> gameContexts;
    private final Map<UUID, GameKey> entityGameKeyMap;

    public GameContextProvider() {
        this.games = new HashMap<>();
        this.gameContexts = new HashMap<>();
        this.entityGameKeyMap = new HashMap<>();
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
     * Assigns the open mode instance to the provider. This will only assign the open mode once, as there should only
     * be one instance. Returns {@code true} if the instance was assigned, and {@code false} if there already is an
     * assigned instance.
     *
     * @param openMode the open mode instance
     * @return whether the instance was assigned
     */
    public boolean assignOpenMode(OpenMode openMode) {
        GameKey gameKey = GameKey.ofOpenMode();
        boolean containsOpenMode = games.keySet().stream().anyMatch(k -> k.equals(gameKey));

        if (containsOpenMode) {
            return false;
        }

        games.put(gameKey, openMode);
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

    public Optional<GameKey> getGameKeyByEntityId(UUID entityId) {
        if (!entityGameKeyMap.containsKey(entityId)) {
            return Optional.empty();
        }

        return Optional.of(entityGameKeyMap.get(entityId));
    }

    public void registerEntity(UUID uniqueId, GameKey gameKey) {
        entityGameKeyMap.put(uniqueId, gameKey);
    }

    public void unregisterEntity(UUID uniqueId) {
        entityGameKeyMap.remove(uniqueId);
    }

    /**
     * Removes a session instance from the provider.
     *
     * @param id the id of the session to remove
     * @return whether the session was removed
     */
    public boolean removeArena(int id) {
        GameKey gameKey = GameKey.ofArena(id);
        Optional<GameKey> sessionGameKey = games.keySet().stream().filter(k -> k.equals(gameKey)).findFirst();

        return sessionGameKey.filter(key -> games.remove(key) != null).isPresent();
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
