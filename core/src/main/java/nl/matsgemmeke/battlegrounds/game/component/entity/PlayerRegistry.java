package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayerRegistry extends EntityRegistry<GamePlayer, Player> {

    void deregister(UUID playerUuid);
}
