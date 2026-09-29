package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.Optional;
import java.util.function.Supplier;

public class GameScope implements Scope {

    private final ThreadLocal<GameContext> currentGameContext = new ThreadLocal<>();

    public Optional<GameContext> getCurrentGameContext() {
        return Optional.ofNullable(currentGameContext.get());
    }

    @Override
    public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
        return () -> this.getCurrentGameContext()
                .orElseThrow(() -> new OutOfScopeException("Cannot access %s because no GameContext is active in GameScope".formatted(key.getTypeLiteral())))
                .getScopedObject(key, unscoped);
    }

    public void runInScope(GameContext gameContext, Runnable action) {
        this.supplyInScope(gameContext, () -> {
            action.run();
            return null;
        });
    }

    public <T> T supplyInScope(GameContext gameContext, Supplier<T> action) {
        GameContext previous = currentGameContext.get();
        currentGameContext.set(gameContext);

        try {
            return action.get();
        } finally {
            if (previous == null) {
                currentGameContext.remove();
            } else {
                currentGameContext.set(previous);
            }
        }
    }
}
