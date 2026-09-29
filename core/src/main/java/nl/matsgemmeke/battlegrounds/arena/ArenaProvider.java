package nl.matsgemmeke.battlegrounds.arena;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameScope;

public class ArenaProvider implements Provider<Arena> {

    private final GameScope gameScope;

    @Inject
    public ArenaProvider(GameScope gameScope) {
        this.gameScope = gameScope;
    }

    @Override
    public Arena get() {
        GameContext context = gameScope.getCurrentGameContext().orElseThrow(() -> new OutOfScopeException("Unable to provide Arena instance as no game context is entered"));

        if (!(context instanceof ArenaGameContext arenaContext)) {
            throw new IllegalStateException("Arena requested but current GameContext (%s) is not an arena context".formatted(context.getGameKey()));
        }

        return arenaContext.getArena();
    }
}
