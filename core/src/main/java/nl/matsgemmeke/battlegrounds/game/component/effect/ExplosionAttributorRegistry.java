package nl.matsgemmeke.battlegrounds.game.component.effect;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ExplosionAttributorRegistry {

    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final List<ExplosionAttributor> attributors;

    @Inject
    public ExplosionAttributorRegistry(GameContextProvider gameContextProvider, GameKey gameKey) {
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.attributors = new LinkedList<>();
    }

    public void addAttributor(ExplosionAttributor attributor) {
        attributors.add(attributor);
        gameContextProvider.registerEntity(attributor.entityId(), gameKey);
    }

    public void removeAttributor(ExplosionAttributor attributor) {
        attributors.remove(attributor);
        gameContextProvider.unregisterEntity(attributor.entityId());
    }

    public boolean isAttributor(UUID entityId) {
        return attributors.stream().anyMatch(attributor -> attributor.entityId() == entityId);
    }
}
