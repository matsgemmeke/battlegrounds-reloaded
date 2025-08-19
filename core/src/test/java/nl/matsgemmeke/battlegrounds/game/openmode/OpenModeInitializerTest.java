package nl.matsgemmeke.battlegrounds.game.openmode;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.event.EntityDamageEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OpenModeInitializerTest {

    private BattlegroundsConfiguration configuration;
    private EventDispatcher eventDispatcher;
    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Provider<CollisionDetector> collisionDetectorProvider;
    private Provider<PlayerRegistry> playerRegistryProvider;
    private Provider<StatePersistenceHandler> statePersistenceHandlerProvider;
    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    public void setUp() {
        configuration = mock(BattlegroundsConfiguration.class);
        eventDispatcher = mock(EventDispatcher.class);
        gameContextProvider = new GameContextProvider();
        gameScope = mock(GameScope.class);
        collisionDetectorProvider = mock();
        playerRegistryProvider = mock();
        statePersistenceHandlerProvider = mock();
        bukkit = mockStatic(Bukkit.class);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void getCreatesNewOpenModeInstanceAndAssignsItToGameContextProvider() {
        UUID playerId = UUID.randomUUID();
        GamePlayer gamePlayer = mock(GamePlayer.class);
        StatePersistenceHandler statePersistenceHandler = mock(StatePersistenceHandler.class);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.registerEntity(player)).thenReturn(gamePlayer);
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);

        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        when(collisionDetectorProvider.get()).thenReturn(collisionDetector);

        when(configuration.isEnabledRegisterPlayersAsPassive()).thenReturn(true);
        when(statePersistenceHandlerProvider.get()).thenReturn(statePersistenceHandler);

        bukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(player));

        OpenModeInitializer openModeInitializer = new OpenModeInitializer(configuration, eventDispatcher, gameContextProvider, gameScope, collisionDetectorProvider, playerRegistryProvider, statePersistenceHandlerProvider);
        openModeInitializer.initialize();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(any(GameContext.class), runnableCaptor.capture());

        runnableCaptor.getValue().run();;

        assertThat(gameContextProvider.getGameContext(GameKey.ofOpenMode())).hasValueSatisfying(gameContext ->
                assertThat(gameContext.getType()).isEqualTo(GameContextType.OPEN_MODE)
        );
        assertThat(gameContextProvider.getGameKeyByEntityId(playerId)).hasValueSatisfying(gameKey ->
                assertThat(gameKey).isEqualTo(GameKey.ofOpenMode())
        );

        ArgumentCaptor<EntityDamageEventHandler> entityDamageEventHandlerCaptor = ArgumentCaptor.forClass(EntityDamageEventHandler.class);
        verify(eventDispatcher).registerEventHandler(eq(EntityDamageEvent.class), entityDamageEventHandlerCaptor.capture());

        verify(gamePlayer).setPassive(true);
        verify(statePersistenceHandler).loadPlayerState(gamePlayer);
    }
}
