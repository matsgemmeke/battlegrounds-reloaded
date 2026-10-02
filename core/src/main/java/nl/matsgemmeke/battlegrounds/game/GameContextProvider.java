package nl.matsgemmeke.battlegrounds.game;

import java.util.*;

/**
 * Stores and provides {@link Game} context to client classes.
 */
public class GameContextProvider {

    private final Map<GameKey, GameContext> gameContexts;
    private final Map<UUID, GameKey> entityGameRegistries;

    public GameContextProvider() {
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
     * @param gameKey the game key of the context to remove
     * @return        whether the context was removed
     */
    public boolean removeGameContext(GameKey gameKey) {
        return gameContexts.remove(gameKey) != null;
    }
}
