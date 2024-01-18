package com.github.matsgemmeke.battlegrounds;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.api.game.Session;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultBattleContextProvider implements BattleContextProvider {

    private List<FreemodeContext> freemodeContexts;
    private Map<Integer, Session> sessions;

    public DefaultBattleContextProvider() {
        this.freemodeContexts = new ArrayList<>();
        this.sessions = new HashMap<>();
    }

    public boolean addFreemodeContext(@NotNull FreemodeContext context) {
        return freemodeContexts.add(context);
    }

    public boolean addSession(@NotNull Session session) {
        int id = session.getId();
        sessions.put(id, session);
        return sessions.get(id) != null;
    }

    @Nullable
    public BattleContext getContext(Player player) {
        for (FreemodeContext context : freemodeContexts) {
            if (context.hasPlayer(player)) {
                return context;
            }
        }

        for (Session session : sessions.values()) {
            if (session.hasPlayer(player)) {
                return session;
            }
        }

        return null;
    }

    @NotNull
    public Collection<BattleContext> getContexts() {
        return Collections.unmodifiableList(Stream.concat(freemodeContexts.stream(), sessions.values().stream()).collect(Collectors.toList()));
    }

    @Nullable
    public Session getSession(int id) {
        return sessions.get(id);
    }

    public boolean removeFreemodeContext(@NotNull FreemodeContext context) {
        return freemodeContexts.remove(context);
    }

    public boolean removeSession(@NotNull Session session) {
        return sessions.remove(session.getId()) != null;
    }
}
