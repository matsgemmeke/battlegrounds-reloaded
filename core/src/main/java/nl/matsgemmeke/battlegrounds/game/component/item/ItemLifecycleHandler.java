package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.jetbrains.annotations.NotNull;

public interface ItemLifecycleHandler {

    void cleanupItems(@NotNull GamePlayer gamePlayer);
}
