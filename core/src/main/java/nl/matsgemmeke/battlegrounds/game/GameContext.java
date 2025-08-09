package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.Provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameContext {

    private final GameContextType type;
    private final Map<Key<?>, Object> scopedObjects;

    public GameContext(GameContextType type) {
        this.type = type;
        this.scopedObjects = new ConcurrentHashMap<>();
    }

    public GameContextType getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(Key<T> key, Provider<T> creator) {
        return (T) scopedObjects.computeIfAbsent(key, k -> creator.get());
    }
}
