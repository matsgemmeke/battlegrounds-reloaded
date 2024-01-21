package com.github.matsgemmeke.battlegrounds;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.api.game.Session;
import com.github.matsgemmeke.battlegrounds.api.game.TrainingMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
