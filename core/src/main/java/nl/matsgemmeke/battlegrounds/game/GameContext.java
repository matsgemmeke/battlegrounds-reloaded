package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.Provider;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameContext {

    @NotNull
    private final GameContextType type;
    @NotNull
    private final GameKey gameKey;
    @NotNull
    private final Map<Key<?>, Object> scopedObjects;

    public GameContext(@NotNull GameKey gameKey, @NotNull GameContextType type) {
        this.type = type;
        this.gameKey = gameKey;
        this.scopedObjects = new ConcurrentHashMap<>();
    }

    @NotNull
    public GameKey getGameKey() {
        return gameKey;
    }

    @NotNull
    public GameContextType getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getScopedObject(Key<T> key, Provider<T> creator) {
        return (T) scopedObjects.computeIfAbsent(key, k -> creator.get());
    }
}
