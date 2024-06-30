package nl.matsgemmeke.battlegrounds;

import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.game.training.TrainingMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DefaultGameProvider implements GameProvider {

    @NotNull
    private Map<Integer, Session> sessions;
    @Nullable
    private TrainingMode trainingMode;

    public DefaultGameProvider() {
        this.sessions = new HashMap<>();
    }

    public boolean addSession(@NotNull Session session) {
        int id = session.getId();
        sessions.put(id, session);
        return sessions.get(id) != null;
    }

    public boolean assignTrainingMode(@NotNull TrainingMode trainingMode) {
        if (this.trainingMode != null) {
            return false;
        }

        this.trainingMode = trainingMode;
        return true;
    }

    public Game getGame(@NotNull Entity entity) {
        return null;
    }

    @Nullable
    public Game getGame(@NotNull Player player) {
        if (trainingMode != null && trainingMode.hasPlayer(player)) {
            return trainingMode;
        }

        for (Session session : sessions.values()) {
            if (session.hasPlayer(player)) {
                return session;
            }
        }

        return null;
    }

    @NotNull
    public Collection<Game> getGames() {
        return Stream.concat(Stream.of(trainingMode), sessions.values().stream()).toList();
    }

    @Nullable
    public Session getSession(int id) {
        return sessions.get(id);
    }

    public boolean removeSession(@NotNull Session session) {
        return sessions.remove(session.getId()) != null;
    }
}
