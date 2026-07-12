package nl.matsgemmeke.battlegrounds.arena;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.arena.Arena;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class ArenaRegistry {

    private final GameContextProvider gameContextProvider;
    private final Map<GameKey, Arena> arenas;

    @Inject
    public ArenaRegistry(GameContextProvider gameContextProvider) {
        this.gameContextProvider = gameContextProvider;
        this.arenas = new HashMap<>();
    }

    public void addArena(GameKey gameKey, Arena arena) {
        arenas.put(gameKey, arena);
        gameContextProvider.addArena(gameKey, arena);
    }

    public Optional<Arena> getArena(int id) {
        GameKey gameKey = GameKey.ofArena(id);

        return arenas.entrySet().stream()
                .filter(entry -> entry.getKey().equals(gameKey))
                .map(Entry::getValue)
                .findFirst();
    }
}
