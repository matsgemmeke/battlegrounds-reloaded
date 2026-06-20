package nl.matsgemmeke.battlegrounds.game.freeplay.component.player;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OpenModePlayerLifecycleHandler implements PlayerLifecycleHandler {

    private final BattlegroundsConfiguration configuration;
    private final ItemLifecycleHandler itemLifecycleHandler;
    private final PlayerRegistry playerRegistry;
    private final StatePersistenceHandler statePersistenceHandler;

    @Inject
    public OpenModePlayerLifecycleHandler(
            BattlegroundsConfiguration configuration,
            ItemLifecycleHandler itemLifecycleHandler,
            PlayerRegistry playerRegistry,
            StatePersistenceHandler statePersistenceHandler
    ) {
        this.configuration = configuration;
        this.itemLifecycleHandler = itemLifecycleHandler;
        this.playerRegistry = playerRegistry;
        this.statePersistenceHandler = statePersistenceHandler;
    }

    @Override
    public void handlePlayerJoin(Player player) {
        UUID playerId = player.getUniqueId();

        if (playerRegistry.isRegistered(playerId)) {
            return;
        }

        boolean passive = configuration.isEnabledRegisterPlayersAsPassive();

        GamePlayer gamePlayer = playerRegistry.register(player);
        gamePlayer.setPassive(passive);

        statePersistenceHandler.loadPlayerState(gamePlayer);
    }

    @Override
    public void handlePlayerLeave(UUID uniqueId) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(uniqueId).orElse(null);

        if (gamePlayer == null) {
            return;
        }

        statePersistenceHandler.savePlayerState(gamePlayer);
        itemLifecycleHandler.resetItems(gamePlayer);
        playerRegistry.deregister(uniqueId);
    }
}
