package nl.matsgemmeke.battlegrounds.game.openmode.component.player;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenModePlayerLifecycleHandlerTest {

    private static final UUID PLAYER_UNIQUE_ID = UUID.randomUUID();

    @Mock
    private BattlegroundsConfiguration configuration;
    @Mock
    private ItemLifecycleHandler itemLifecycleHandler;
    @Mock
    private PlayerRegistry playerRegistry;
    @Mock
    private StatePersistenceHandler statePersistenceHandler;
    @InjectMocks
    private OpenModePlayerLifecycleHandler playerLifecycleHandler;

    @Test
    void handlePlayerJoinDoesNotRegisterPlayerWhenAlreadyRegistered() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        when(playerRegistry.isRegistered(PLAYER_UNIQUE_ID)).thenReturn(true);

        playerLifecycleHandler.handlePlayerJoin(player);

        verify(playerRegistry, never()).register(any(Player.class));
        verifyNoInteractions(statePersistenceHandler);
    }

    @Test
    void handlePlayerJoinRegistersPlayerAndLoadsState() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);

        when(configuration.isEnabledRegisterPlayersAsPassive()).thenReturn(true);
        when(playerRegistry.isRegistered(PLAYER_UNIQUE_ID)).thenReturn(false);
        when(playerRegistry.register(player)).thenReturn(gamePlayer);

        playerLifecycleHandler.handlePlayerJoin(player);

        verify(gamePlayer).setPassive(true);
        verify(playerRegistry).register(player);
    }

    @Test
    void handlePlayerLeaveDoesNotDeregisterPlayerWhenNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        playerLifecycleHandler.handlePlayerLeave(PLAYER_UNIQUE_ID);

        verify(playerRegistry, never()).deregister(any(UUID.class));
        verifyNoInteractions(itemLifecycleHandler);
        verifyNoInteractions(statePersistenceHandler);
    }

    @Test
    void handlePlayerLeaveDeregistersPlayerAndSavesState() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));

        playerLifecycleHandler.handlePlayerLeave(PLAYER_UNIQUE_ID);

        verify(statePersistenceHandler).savePlayerState(gamePlayer);
        verify(itemLifecycleHandler).cleanupItems(gamePlayer);
        verify(playerRegistry).deregister(PLAYER_UNIQUE_ID);
    }
}
