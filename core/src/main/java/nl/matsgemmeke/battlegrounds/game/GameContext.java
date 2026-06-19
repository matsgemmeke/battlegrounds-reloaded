package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.Provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameContext {

    private final GameContextType type;
    private final GameKey gameKey;
    private final Map<Key<?>, Object> scopedObjects;

    public GameContext(GameKey gameKey, GameContextType type) {
        this.type = type;
        this.gameKey = gameKey;
        this.scopedObjects = new ConcurrentHashMap<>();
    }

    public GameKey getGameKey() {
        return gameKey;
    }

    public GameContextType getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getScopedObject(Key<T> key, Provider<T> creator) {
        T instance = creator.get();

        return (T) scopedObjects.computeIfAbsent(key, k -> instance);
    }
}
