package nl.matsgemmeke.battlegrounds.game.component.storage;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.jetbrains.annotations.NotNull;

public interface StatePersistenceHandler {

    void loadPlayerState(@NotNull GamePlayer gamePlayer);

    void savePlayerState(@NotNull GamePlayer gamePlayer);

    void saveState();
}
