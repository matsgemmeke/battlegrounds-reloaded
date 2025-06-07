package nl.matsgemmeke.battlegrounds.game.openmode.component.player;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class OpenModePlayerLifecycleHandlerTest {

    private BattlegroundsConfiguration configuration;
    private PlayerRegistry playerRegistry;
    private StatePersistenceHandler statePersistenceHandler;

    @BeforeEach
    public void setUp() {
        configuration = mock(BattlegroundsConfiguration.class);
        playerRegistry = mock(PlayerRegistry.class);
        statePersistenceHandler = mock(StatePersistenceHandler.class);
    }

    @Test
    public void handlePlayerJoinRegistersPlayerAndLoadsState() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Player player = mock(Player.class);

        when(configuration.isEnabledRegisterPlayersAsPassive()).thenReturn(true);
        when(playerRegistry.registerEntity(player)).thenReturn(gamePlayer);

        OpenModePlayerLifecycleHandler playerLifecycleHandler = new OpenModePlayerLifecycleHandler(configuration, playerRegistry, statePersistenceHandler);
        playerLifecycleHandler.handlePlayerJoin(player);

        verify(gamePlayer).setPassive(true);
        verify(playerRegistry).registerEntity(player);
    }

    @Test
    public void handlePlayerLeaveDeregistersPlayerAndSavesState() {
        UUID playerUuid = UUID.randomUUID();

        OpenModePlayerLifecycleHandler playerLifecycleHandler = new OpenModePlayerLifecycleHandler(configuration, playerRegistry, statePersistenceHandler);
        playerLifecycleHandler.handlePlayerLeave(playerUuid);

        verify(playerRegistry).deregister(playerUuid);
    }
}
