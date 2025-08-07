package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

public class GameScope implements Scope {

    private final ThreadLocal<GameContext> currentContext = new ThreadLocal<>();

    public void enter(GameContext gameContext) {
        currentContext.set(gameContext);
    }

    public void exit() {
        currentContext.remove();
    }

    @SuppressWarnings("unchecked")
    public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
        return () -> {
            GameContext context = currentContext.get();

            if (context == null) {
                throw new OutOfScopeException("No GameContext in scope for key: " + key);
            }

            return context.getOrCreate(key, unscoped);
        };
    }
}
