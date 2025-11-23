package nl.matsgemmeke.battlegrounds.game.openmode.component.player;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class OpenModePlayerLifecycleHandlerTest {

    private static final UUID PLAYER_UNIQUE_ID = UUID.randomUUID();

    private BattlegroundsConfiguration configuration;
    private ItemLifecycleHandler itemLifecycleHandler;
    private PlayerRegistry playerRegistry;
    private StatePersistenceHandler statePersistenceHandler;

    @BeforeEach
    public void setUp() {
        configuration = mock(BattlegroundsConfiguration.class);
        itemLifecycleHandler = mock(ItemLifecycleHandler.class);
        playerRegistry = mock(PlayerRegistry.class);
        statePersistenceHandler = mock(StatePersistenceHandler.class);
    }

    @Test
    public void handlePlayerJoinDoesNotRegisterPlayerWhenAlreadyRegistered() {
        Player player = mock(Player.class);

        when(playerRegistry.isRegistered(player)).thenReturn(true);

        OpenModePlayerLifecycleHandler playerLifecycleHandler = new OpenModePlayerLifecycleHandler(configuration, itemLifecycleHandler, playerRegistry, statePersistenceHandler);
        playerLifecycleHandler.handlePlayerJoin(player);

        verify(playerRegistry, never()).register(any(Player.class));
        verifyNoInteractions(statePersistenceHandler);
    }

    @Test
    public void handlePlayerJoinRegistersPlayerAndLoadsState() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Player player = mock(Player.class);

        when(configuration.isEnabledRegisterPlayersAsPassive()).thenReturn(true);
        when(playerRegistry.register(player)).thenReturn(gamePlayer);

        OpenModePlayerLifecycleHandler playerLifecycleHandler = new OpenModePlayerLifecycleHandler(configuration, itemLifecycleHandler, playerRegistry, statePersistenceHandler);
        playerLifecycleHandler.handlePlayerJoin(player);

        verify(gamePlayer).setPassive(true);
        verify(playerRegistry).register(player);
    }

    @Test
    public void handlePlayerLeaveDoesNotDeregisterPlayerWhenNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        OpenModePlayerLifecycleHandler playerLifecycleHandler = new OpenModePlayerLifecycleHandler(configuration, itemLifecycleHandler, playerRegistry, statePersistenceHandler);
        playerLifecycleHandler.handlePlayerLeave(PLAYER_UNIQUE_ID);

        verify(playerRegistry, never()).deregister(any(UUID.class));
        verifyNoInteractions(itemLifecycleHandler);
        verifyNoInteractions(statePersistenceHandler);
    }

    @Test
    public void handlePlayerLeaveDeregistersPlayerAndSavesState() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));

        OpenModePlayerLifecycleHandler playerLifecycleHandler = new OpenModePlayerLifecycleHandler(configuration, itemLifecycleHandler, playerRegistry, statePersistenceHandler);
        playerLifecycleHandler.handlePlayerLeave(PLAYER_UNIQUE_ID);

        verify(statePersistenceHandler).savePlayerState(gamePlayer);
        verify(itemLifecycleHandler).cleanupItems(gamePlayer);
        verify(playerRegistry).deregister(PLAYER_UNIQUE_ID);
    }
}
