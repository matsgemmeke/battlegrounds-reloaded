package nl.matsgemmeke.battlegrounds.game.openmode;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.event.EntityDamageEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenModeInitializerTest {

    @Mock
    private BattlegroundsConfiguration configuration;
    @Mock
    private EventDispatcher eventDispatcher;
    @Spy
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Provider<PlayerRegistry> playerRegistryProvider;
    @Mock
    private Provider<StatePersistenceHandler> statePersistenceHandlerProvider;
    @Mock
    private Provider<EntityDamageEventHandler> entityDamageEventHandlerProvider;

    private MockedStatic<Bukkit> bukkit;

    private OpenModeInitializer openModeInitializer;

    @BeforeEach
    void setUp() {
        bukkit = mockStatic(Bukkit.class);

        openModeInitializer = new OpenModeInitializer(configuration, eventDispatcher, gameContextProvider, gameScope, playerRegistryProvider, statePersistenceHandlerProvider, entityDamageEventHandlerProvider);
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    @Test
    @DisplayName("get creates new OpenMode instance and assigns it to the game context provider")
    void get_successful() {
        UUID playerId = UUID.randomUUID();
        GamePlayer gamePlayer = mock(GamePlayer.class);
        StatePersistenceHandler statePersistenceHandler = mock(StatePersistenceHandler.class);
        EntityDamageEventHandler entityDamageEventHandler = mock(EntityDamageEventHandler.class);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.register(player)).thenReturn(gamePlayer);

        when(configuration.isEnabledRegisterPlayersAsPassive()).thenReturn(true);
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(statePersistenceHandlerProvider.get()).thenReturn(statePersistenceHandler);
        when(entityDamageEventHandlerProvider.get()).thenReturn(entityDamageEventHandler);

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(any(GameContext.class), any(Runnable.class));

        bukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(player));

        openModeInitializer.initialize();

        assertThat(gameContextProvider.getGameContext(GameKey.ofOpenMode())).hasValueSatisfying(gameContext ->
                assertThat(gameContext.getType()).isEqualTo(GameContextType.OPEN_MODE)
        );
        assertThat(gameContextProvider.getGameKeyByEntityId(playerId)).hasValueSatisfying(gameKey ->
                assertThat(gameKey).isEqualTo(GameKey.ofOpenMode())
        );

        verify(eventDispatcher).registerEventHandler(EntityDamageEvent.class, entityDamageEventHandler);
        verify(gamePlayer).setPassive(true);
        verify(statePersistenceHandler).loadPlayerState(gamePlayer);
    }
}
