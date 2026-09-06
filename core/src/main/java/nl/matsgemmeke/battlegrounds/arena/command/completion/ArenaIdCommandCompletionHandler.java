package nl.matsgemmeke.battlegrounds.arena.command.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.command.CommandCompletionHandler;

import java.util.Collection;

public class ArenaIdCommandCompletionHandler implements CommandCompletionHandler {

    private final ArenaRegistry arenaRegistry;

    @Inject
    public ArenaIdCommandCompletionHandler(ArenaRegistry arenaRegistry) {
        this.arenaRegistry = arenaRegistry;
    }
    
    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) {
        return arenaRegistry.getArenaIds().stream().map(Object::toString).toList();
    }
}
