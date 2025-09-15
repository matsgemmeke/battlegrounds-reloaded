package nl.matsgemmeke.battlegrounds.game.component.player;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayerLifecycleHandler {

    void handlePlayerJoin(Player player);

    void handlePlayerLeave(UUID uniqueId);
}
