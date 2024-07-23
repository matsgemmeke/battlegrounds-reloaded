package nl.matsgemmeke.battlegrounds;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class GameContextProvider {

    @NotNull
    private Map<Integer, GameContext> sessionContexts;
    @Nullable
    private GameContext trainingModeContext;

    public GameContextProvider() {
        this.sessionContexts = new HashMap<>();
    }

    public boolean addSessionContext(int id, @NotNull GameContext context) {
        sessionContexts.put(id, context);
        return sessionContexts.get(id) != null;
    }

    public boolean assignTrainingModeContext(@NotNull GameContext trainingModeContext) {
        if (this.trainingModeContext != null) {
            return false;
        }

        this.trainingModeContext = trainingModeContext;
        return true;
    }

    @Nullable
    public GameContext getContext(@NotNull Player player) {
        for (GameContext context : this.getContexts()) {
            if (context.getPlayerRegistry().isRegistered(player)) {
                return context;
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

    @Nullable
    public GameContext getSessionContext(int id) {
        return sessionContexts.get(id);
    }

    public boolean removeSessionContext(int id) {
        return sessionContexts.remove(id) != null;
    }
}
