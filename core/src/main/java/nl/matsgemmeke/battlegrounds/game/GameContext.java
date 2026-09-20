package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.Provider;

import java.util.HashMap;
import java.util.Map;

public class GameContext {

    private final GameContextType type;
    private final GameKey gameKey;
    private final Map<Key<?>, Object> scopedObjects;

    public GameContext(GameKey gameKey, GameContextType type) {
        this.type = type;
        this.gameKey = gameKey;
        this.scopedObjects = new HashMap<>();
    }

    public GameKey getGameKey() {
        return gameKey;
    }

    public GameContextType getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getScopedObject(Key<T> key, Provider<T> unscoped) {
        Object object = scopedObjects.get(key);

        if (object == null) {
            object = unscoped.get();
            scopedObjects.put(key, object);
        }

        return (T) object;
    }
}
