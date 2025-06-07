package nl.matsgemmeke.battlegrounds.game.openmode.component.player;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OpenModePlayerLifecycleHandler implements PlayerLifecycleHandler {

    @NotNull
    private final BattlegroundsConfiguration configuration;
    @NotNull
    private final PlayerRegistry playerRegistry;
    @NotNull
    private final StatePersistenceHandler statePersistenceHandler;

    @Inject
    public OpenModePlayerLifecycleHandler(
            @NotNull BattlegroundsConfiguration configuration,
            @Assisted @NotNull PlayerRegistry playerRegistry,
            @Assisted @NotNull StatePersistenceHandler statePersistenceHandler
    ) {
        this.configuration = configuration;
        this.playerRegistry = playerRegistry;
        this.statePersistenceHandler = statePersistenceHandler;
    }

    public void handlePlayerJoin(@NotNull Player player) {
        boolean passive = configuration.isEnabledRegisterPlayersAsPassive();

        GamePlayer gamePlayer = playerRegistry.registerEntity(player);
        gamePlayer.setPassive(passive);
    }

    public void handlePlayerLeave(@NotNull UUID playerUuid) {
        playerRegistry.deregister(playerUuid);
    }
}
