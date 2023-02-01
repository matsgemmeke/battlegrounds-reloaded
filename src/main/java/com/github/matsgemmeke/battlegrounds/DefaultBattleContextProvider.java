package com.github.matsgemmeke.battlegrounds;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultBattleContextProvider implements BattleContextProvider {

    private List<FreemodeContext> freemodeContexts;
    private Map<Integer, GameContext> gameContexts;

    public DefaultBattleContextProvider() {
        this.freemodeContexts = new ArrayList<>();
        this.gameContexts = new HashMap<>();
    }

    public boolean addFreemodeContext(@NotNull FreemodeContext context) {
        return freemodeContexts.add(context);
    }

    public boolean addGameContext(@NotNull GameContext gameContext) {
        int id = gameContext.getId();
        gameContexts.put(id, gameContext);
        return gameContexts.get(id) != null;
    }

    @Nullable
    public BattleContext getContext(Player player) {
        for (FreemodeContext context : freemodeContexts) {
            if (context.hasPlayer(player)) {
                return context;
            }
        }

        for (GameContext context : gameContexts.values()) {
            if (context.hasPlayer(player)) {
                return context;
            }
        }

        return null;
    }

    @NotNull
    public Collection<BattleContext> getContexts() {
        return Collections.unmodifiableList(Stream.concat(freemodeContexts.stream(), gameContexts.values().stream()).collect(Collectors.toList()));
    }

    @Nullable
    public GameContext getGameContext(int id) {
        return gameContexts.get(id);
    }

    public boolean removeFreemodeContext(@NotNull FreemodeContext context) {
        return freemodeContexts.remove(context);
    }

    public boolean removeGameContext(@NotNull GameContext context) {
        return gameContexts.remove(context.getId()) != null;
    }
}
