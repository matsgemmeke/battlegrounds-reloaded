package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class GameKeyProvider implements Provider<GameKey> {

    @NotNull
    private final GameScope gameScope;

    @Inject
    public GameKeyProvider(@NotNull GameScope gameScope) {
        this.gameScope = gameScope;
    }

    public GameKey get() {
        GameContext gameContext = gameScope.getCurrentGameContext()
                .orElseThrow(() -> new OutOfScopeException("No game context active"));

        return gameContext.getGameKey();
    }
}
