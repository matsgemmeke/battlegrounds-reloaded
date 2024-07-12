package nl.matsgemmeke.battlegrounds.game.access.provider;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import nl.matsgemmeke.battlegrounds.game.access.DefaultActionHandler;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ActionHandlerProvider {

    @NotNull
    private ConcurrentMap<Game, ActionHandler> gameActionHandlers;
    @NotNull
    private GameProvider gameProvider;

    public ActionHandlerProvider(@NotNull GameProvider gameProvider) {
        this.gameProvider = gameProvider;
        this.gameActionHandlers = new ConcurrentHashMap<>();
    }

    @Nullable
    public ActionHandler getActionHandler(@NotNull Player player) {
        Game game = gameProvider.getGame(player);

        if (game == null) {
            return null;
        }

        gameActionHandlers.putIfAbsent(game, new DefaultActionHandler(game));

        return gameActionHandlers.get(game);
    }
}
