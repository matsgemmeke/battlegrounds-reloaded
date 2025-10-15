package nl.matsgemmeke.battlegrounds.game.component.storage;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;

public interface StatePersistenceHandler {

    void loadPlayerState(GamePlayer gamePlayer);

    void savePlayerState(GamePlayer gamePlayer);

    void saveState();
}
