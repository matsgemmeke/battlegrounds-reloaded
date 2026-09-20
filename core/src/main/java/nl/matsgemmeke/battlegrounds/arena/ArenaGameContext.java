package nl.matsgemmeke.battlegrounds.arena;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;

public class ArenaGameContext extends GameContext {

    private final Arena arena;

    public ArenaGameContext(GameKey gameKey, Arena arena) {
        super(gameKey, GameContextType.ARENA_MODE);
        this.arena = arena;
    }

    public Arena getArena() {
        return arena;
    }
}
