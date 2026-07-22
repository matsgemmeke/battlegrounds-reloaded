package nl.matsgemmeke.battlegrounds.arena.command.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.CommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;

import java.util.Collection;

public class ArenaIdCommandCompletionHandler implements CommandCompletionHandler {

    private final GameContextProvider gameContextProvider;

    @Inject
    public ArenaIdCommandCompletionHandler(GameContextProvider gameContextProvider) {
        this.gameContextProvider = gameContextProvider;
    }
    
    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) {
        return gameContextProvider.getArenaIds().stream().map(Object::toString).toList();
    }
}
