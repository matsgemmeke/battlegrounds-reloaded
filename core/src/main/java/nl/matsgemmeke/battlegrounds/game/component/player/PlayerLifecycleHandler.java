package nl.matsgemmeke.battlegrounds.game.component.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerLifecycleHandler {

    void handlePlayerJoin(@NotNull Player player);

    void handlePlayerLeave(@NotNull UUID playerUuid);
}
