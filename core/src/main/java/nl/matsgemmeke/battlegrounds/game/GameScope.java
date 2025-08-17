package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.Optional;

public class GameScope implements Scope {

    private final ThreadLocal<GameContext> currentGameContext = new ThreadLocal<>();

    public Optional<GameContext> getCurrentGameContext() {
        return Optional.ofNullable(currentGameContext.get());
    }

    public void enter(GameContext gameContext) {
        currentGameContext.set(gameContext);
    }

    public void exit() {
        currentGameContext.remove();
    }

    public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
        return () -> {
            GameContext context = currentGameContext.get();

            if (context == null) {
                throw new OutOfScopeException("No GameContext in scope for key: " + key);
            }

            return context.getScopedObject(key, unscoped);
        };
    }

    public void runInScope(GameContext gameContext, Runnable action) {
        this.enter(gameContext);

        try {
            action.run();
        } finally {
            this.exit();
        }
    }
}
