package nl.matsgemmeke.battlegrounds.game.openmode.component.player;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OpenModePlayerLifecycleHandler implements PlayerLifecycleHandler {

    @NotNull
    private final BattlegroundsConfiguration configuration;
    @NotNull
    private final ItemLifecycleHandler itemLifecycleHandler;
    @NotNull
    private final PlayerRegistry playerRegistry;
    @NotNull
    private final StatePersistenceHandler statePersistenceHandler;

    @Inject
    public OpenModePlayerLifecycleHandler(
            @NotNull BattlegroundsConfiguration configuration,
            @NotNull ItemLifecycleHandler itemLifecycleHandler,
            @NotNull PlayerRegistry playerRegistry,
            @NotNull StatePersistenceHandler statePersistenceHandler
    ) {
        this.configuration = configuration;
        this.itemLifecycleHandler = itemLifecycleHandler;
        this.playerRegistry = playerRegistry;
        this.statePersistenceHandler = statePersistenceHandler;
    }

    public void handlePlayerJoin(@NotNull Player player) {
        if (playerRegistry.isRegistered(player)) {
            return;
        }

        boolean passive = configuration.isEnabledRegisterPlayersAsPassive();

        GamePlayer gamePlayer = playerRegistry.registerEntity(player);
        gamePlayer.setPassive(passive);

        statePersistenceHandler.loadPlayerState(gamePlayer);
    }

    public void handlePlayerLeave(@NotNull UUID playerUuid) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(playerUuid);

        if (gamePlayer == null) {
            return;
        }

        statePersistenceHandler.savePlayerState(gamePlayer);
        itemLifecycleHandler.cleanupItems(gamePlayer);
        playerRegistry.deregister(playerUuid);
    }
}
