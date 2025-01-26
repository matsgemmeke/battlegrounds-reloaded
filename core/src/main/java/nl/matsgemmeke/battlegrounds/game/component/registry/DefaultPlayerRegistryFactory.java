package nl.matsgemmeke.battlegrounds.game.component.registry;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.jetbrains.annotations.NotNull;

public interface DefaultPlayerRegistryFactory {

    @NotNull
    PlayerRegistry make(@NotNull EntityStorage<GamePlayer> playerStorage);
}
