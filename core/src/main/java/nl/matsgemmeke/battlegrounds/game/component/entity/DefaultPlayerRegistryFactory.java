package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.jetbrains.annotations.NotNull;

public interface DefaultPlayerRegistryFactory {

    @NotNull
    PlayerRegistry create(@NotNull EntityStorage<GamePlayer> playerStorage);
}
