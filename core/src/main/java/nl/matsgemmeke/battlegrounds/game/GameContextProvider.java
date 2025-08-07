package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.game.component.entity.EntityRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.openmode.OpenMode;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Stores and provides {@link Game} context to client classes.
 */
public class GameContextProvider {

    @NotNull
    private Map<GameKey, Game> games;
    @NotNull
    private final Map<GameKey, GameContext> gameContexts;
    @NotNull
    private Map<GameKey, Map<Class<?>, Object>> gameComponents;

    public GameContextProvider() {
        this.games = new HashMap<>();
        this.gameContexts = new HashMap<>();
        this.gameComponents = new HashMap<>();
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
     * Adds a session instance to the provider.
     *
     * @param gameKey the session game key
     * @param session the session to be added
     * @return whether the session was added
     */
    public boolean addSession(@NotNull GameKey gameKey, @NotNull Session session) {
        games.put(gameKey, session);
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
    public boolean assignOpenMode(@NotNull OpenMode openMode) {
        GameKey gameKey = GameKey.ofOpenMode();
        boolean containsOpenMode = games.keySet().stream().anyMatch(k -> k.equals(gameKey));

        if (containsOpenMode) {
            return false;
        }

        games.put(gameKey, openMode);
        gameComponents.put(gameKey, new HashMap<>());
        return true;
    }

    /**
     * Gets a component interface instance based on the given game key and component type.
     *
     * @param gameKey the game key
     * @param componentClass the component type
     * @return the instance of the given component type that is registered under the given game key
     * @param <T> the component type
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T getComponent(@NotNull GameKey gameKey, @NotNull Class<T> componentClass) {
        GameKey gameKeyMatch = this.matchGameKey(gameKey)
                .orElseThrow(() -> new GameKeyNotFoundException("Cannot get component for %s because given game key %s was not found".formatted(componentClass, gameKey)));

        Map<Class<?>, Object> components = gameComponents.get(gameKeyMatch);
        T component = (T) components.get(componentClass);

        if (component == null) {
            throw new GameComponentNotFoundException("Given game key %s has no registered components for %s".formatted(gameKeyMatch, componentClass));
        }

        return component;
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
     * Gets the {@link GameKey} of the game a player is currently in. Returns null if the player is not in any of the
     * registered games.
     *
     * @param player the player
     * @return the game key of the game the player is currently in, or null if the player is not any game
     */
    @Nullable
    public GameKey getGameKey(@NotNull Player player) {
        for (GameKey gameKey : games.keySet()) {

            PlayerRegistry playerRegistry = this.getComponent(gameKey, PlayerRegistry.class);
            if (playerRegistry.isRegistered(player)) {
                return gameKey;
            }
        }
        return null;
    }

    /**
     * Gets the {@link GameKey} of the game a specific entity is currently in by matching their {@link UUID}. Returns
     * {@code null} if none of the games contain an entity whose UUID match.
     *
     * @param uuid the entity uuid
     * @return the game key of the game which contains an entity with the given uuid, or null if none of the
     * games have a matching entity
     */
    @Nullable
    public GameKey getGameKey(@NotNull UUID uuid) {
        for (GameKey gameKey : games.keySet()) {
            for (EntityRegistry<?, ?> entityRegistry : this.getEntityRegistries(gameKey)) {
                if (entityRegistry.isRegistered(uuid)) {
                    return gameKey;
                }
            }
        }
        return null;
    }

    /**
     * Gets the game keys of all registered games.
     *
     * @return all registered game keys
     */
    @NotNull
    public Set<GameKey> getGameKeys() {
        return games.keySet();
    }

    @NotNull
    private Iterable<EntityRegistry<?, ?>> getEntityRegistries(@NotNull GameKey gameKey) {
        PlayerRegistry playerRegistry = this.getComponent(gameKey, PlayerRegistry.class);

        return List.of(playerRegistry);
    }

    /**
     * Registers a game component interface to the provider.
     *
     * @param gameKey the game key
     * @param componentClass the class of the component interface
     * @param componentInstance the instance of the component interface
     * @param <T> the component type
     */
    public <T> void registerComponent(@NotNull GameKey gameKey, @NotNull Class<T> componentClass, @NotNull T componentInstance) {
        GameKey gameKeyMatch = this.matchGameKey(gameKey)
                .orElseThrow(() -> new GameKeyNotFoundException("Cannot register component because given game key " + gameKey + " was not found"));

        Map<Class<?>, Object> components = gameComponents.get(gameKeyMatch);
        components.put(componentClass, componentInstance);
    }

    /**
     * Registers a {@link Game} instance to the provider.
     *
     * @param gameKey the game key
     * @param game the game
     */
    public void registerGame(@NotNull GameKey gameKey, @NotNull Game game) {
        games.put(gameKey, game);
        gameComponents.put(gameKey, new HashMap<>());
    }

    /**
     * Removes a session instance from the provider.
     *
     * @param id the id of the session to remove
     * @return whether the session was removed
     */
    public boolean removeSession(int id) {
        GameKey gameKey = GameKey.ofSession(id);
        Optional<GameKey> sessionGameKey = games.keySet().stream().filter(k -> k.equals(gameKey)).findFirst();

        return sessionGameKey.filter(key -> games.remove(key) != null).isPresent();
    }

    /**
     * Gets whether a session instance exists by matching an id. Returns {@code true} if a session by the given id
     * exists, and {@code false} if not.
     *
     * @param id the session id
     * @return whether a session with the given id exists
     */
    public boolean sessionExists(int id) {
        GameKey sessionGameKey = GameKey.ofSession(id);

        for (GameKey gameKey : games.keySet()) {
            if (gameKey.equals(sessionGameKey)) {
                return true;
            }
        }

        return false;
    }

    public void shutdown() {
        OpenMode openMode = this.getOpenMode();

        if (openMode != null) {
            GameKey gameKey = GameKey.ofOpenMode();

            StatePersistenceHandler statePersistenceHandler = this.getComponent(gameKey, StatePersistenceHandler.class);
            statePersistenceHandler.saveState();
        }
    }

    @Nullable
    private OpenMode getOpenMode() {
        GameKey gameKey = GameKey.ofOpenMode();

        return (OpenMode) games.entrySet().stream()
                .filter(entry -> entry.getKey().equals(gameKey))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    @NotNull
    private Optional<GameKey> matchGameKey(@NotNull GameKey gameKey) {
        return games.keySet().stream().filter(k -> k.equals(gameKey)).findFirst();
    }
}
