package nl.matsgemmeke.battlegrounds.game.component.storage;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.jetbrains.annotations.NotNull;

public interface StatePersistenceHandler {

    void loadState(@NotNull GamePlayer gamePlayer);

    void saveState();
}
