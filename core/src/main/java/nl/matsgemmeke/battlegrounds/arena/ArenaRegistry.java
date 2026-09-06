package nl.matsgemmeke.battlegrounds.arena;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;

import java.util.HashMap;
import java.util.List;
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
        GameContext gameContext = new GameContext(gameKey, GameContextType.ARENA_MODE);
        gameContextProvider.addGameContext(gameKey, gameContext);

        arenas.put(gameKey, arena);
    }

    public Optional<Arena> getArena(int id) {
        GameKey gameKey = GameKey.ofArena(id);

        return arenas.entrySet().stream()
                .filter(entry -> entry.getKey().equals(gameKey))
                .map(Entry::getValue)
                .findFirst();
    }

    public List<Integer> getArenaIds() {
        return arenas.values().stream().map(Arena::getId).toList();
    }
}
