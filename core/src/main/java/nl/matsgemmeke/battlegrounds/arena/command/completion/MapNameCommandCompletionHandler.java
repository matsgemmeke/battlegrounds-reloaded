package nl.matsgemmeke.battlegrounds.arena.command.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.command.CommandCompletionHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MapNameCommandCompletionHandler implements CommandCompletionHandler {

    private static final List<String> EMPTY = Collections.emptyList();

    private final ArenaRegistry arenaRegistry;

    @Inject
    public MapNameCommandCompletionHandler(ArenaRegistry arenaRegistry) {
        this.arenaRegistry = arenaRegistry;
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) {
        List<String> args = context.getArgs();
        String rawArenaId = args.get(0);

        if (rawArenaId == null || rawArenaId.isBlank()) {
            return EMPTY;
        }

        int arenaId;

        try {
            arenaId = Integer.parseInt(rawArenaId);
        } catch (NumberFormatException e) {
            return EMPTY;
        }

        Arena arena = arenaRegistry.getArena(arenaId).orElse(null);

        if (arena == null) {
            return EMPTY;
        }

        return arena.getMapNames();
    }
}
