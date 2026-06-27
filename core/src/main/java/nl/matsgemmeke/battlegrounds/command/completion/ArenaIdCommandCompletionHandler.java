package nl.matsgemmeke.battlegrounds.command.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions.CommandCompletionHandler;
import co.aikar.commands.InvalidCommandArgument;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;

import java.util.Collection;

public class ArenaIdCommandCompletionHandler implements CommandCompletionHandler<BukkitCommandCompletionContext> {

    private final GameContextProvider gameContextProvider;

    @Inject
    public ArenaIdCommandCompletionHandler(GameContextProvider gameContextProvider) {
        this.gameContextProvider = gameContextProvider;
    }
    
    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return gameContextProvider.getArenaIds().stream().map(Object::toString).toList();
    }
}
