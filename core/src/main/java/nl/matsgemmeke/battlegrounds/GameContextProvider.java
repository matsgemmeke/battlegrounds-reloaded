package nl.matsgemmeke.battlegrounds;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * Stores and provides {@link GameContext} instances to outside classes.
 */
public class GameContextProvider {

    @NotNull
    private Map<Integer, GameContext> sessionContexts;
    @Nullable
    private GameContext trainingModeContext;

    public GameContextProvider() {
        this.sessionContexts = new HashMap<>();
    }

    /**
     * Adds a session context instance to the provider.
     *
     * @param id the session id
     * @param context the session context to be added
     * @return whether the session was added
     */
    public boolean addSessionContext(int id, @NotNull GameContext sessionContext) {
        sessionContexts.put(id, sessionContext);
        return sessionContexts.get(id) != null;
    }

    /**
     * Adds a training mode instance to the provider. There should only be one instance assigned.
     *
     * @param trainingModeContext the context instance to be assigned
     * @return whether the instance was assigned
     */
    public boolean assignTrainingModeContext(@NotNull GameContext trainingModeContext) {
        if (this.trainingModeContext != null) {
            return false;
        }

        this.trainingModeContext = trainingModeContext;
        return true;
    }

    /**
     * Gets the {@link GameContext} a player is currently in. Returns null if the player is not in any of the
     * registered contexts.
     *
     * @param player the player
     * @return the context the player is currently in, or null if the player is not present in a context instance
     */
    @Nullable
    public GameContext getContext(@NotNull Player player) {
        for (GameContext context : this.getContexts()) {
            if (context.getPlayerRegistry().isRegistered(player)) {
                return context;
            }
        }

        return null;
    }

    /**
     * Gets the {@link GameContext} a specific entity is currently in by matching their {@link UUID}. Returns null if
     * none of the contexts contain an entity whose UUID match with the given id.
     *
     * @param uuid the uuid
     * @return the context which contains an entity with the given uuid is, or if none of the contexts have a matching
     * entity
     */
    @Nullable
    public GameContext getContext(@NotNull UUID uuid) {
        for (GameContext context : this.getContexts()) {
            for (EntityRegistry<?, ?> entityRegistry : this.getEntityRegistries(context)) {
                if (entityRegistry.isRegistered(uuid)) {
                    return context;
                }
            }
        }
        return null;
    }

    @NotNull
    private Collection<GameContext> getContexts() {
        if (trainingModeContext == null) {
            return sessionContexts.values();
        }

        return Stream.concat(Stream.of(trainingModeContext), sessionContexts.values().stream()).toList();
    }

    @NotNull
    private Iterable<EntityRegistry<?, ?>> getEntityRegistries(@NotNull GameContext context) {
        return List.of(context.getPlayerRegistry());
    }

    /**
     * Gets a session {@link GameContext} instance by matching an id. Returns null if the provider does not contain a
     * matching session context instance.
     *
     * @param id the session id
     * @return the session context instance or null if the provider does not have an instance with the id
     */
    @Nullable
    public GameContext getSessionContext(int id) {
        return sessionContexts.get(id);
    }

    /**
     * Removes a session {@link GameContext} instance from the provider.
     *
     * @param id the id of the session to remove
     * @return whether the session was removed
     */
    public boolean removeSessionContext(int id) {
        return sessionContexts.remove(id) != null;
    }
}
