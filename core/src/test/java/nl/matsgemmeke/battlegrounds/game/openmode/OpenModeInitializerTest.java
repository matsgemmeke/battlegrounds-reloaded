package nl.matsgemmeke.battlegrounds.game.openmode;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.entity.DefaultPlayerRegistryFactory;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.event.EntityDamageEventHandler;
import nl.matsgemmeke.battlegrounds.game.openmode.component.player.OpenModePlayerLifecycleHandlerFactory;
import nl.matsgemmeke.battlegrounds.game.openmode.component.storage.OpenModeStatePersistenceHandlerFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.List;

import static org.mockito.Mockito.*;

public class OpenModeInitializerTest {

    private BattlegroundsConfiguration configuration;
    private EventDispatcher eventDispatcher;
    private GameContextProvider contextProvider;
    private DefaultPlayerRegistryFactory playerRegistryFactory;
    private Provider<CollisionDetector> collisionDetectorProvider;
    private OpenModePlayerLifecycleHandlerFactory playerLifecycleHandlerFactory;
    private OpenModeStatePersistenceHandlerFactory statePersistenceHandlerFactory;
    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    public void setUp() {
        configuration = mock(BattlegroundsConfiguration.class);
        eventDispatcher = mock(EventDispatcher.class);
        contextProvider = new GameContextProvider();
        playerRegistryFactory = mock(DefaultPlayerRegistryFactory.class);
        collisionDetectorProvider = mock();
        playerLifecycleHandlerFactory = mock(OpenModePlayerLifecycleHandlerFactory.class);
        statePersistenceHandlerFactory = mock(OpenModeStatePersistenceHandlerFactory.class);
        bukkit = mockStatic(Bukkit.class);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void getCreatesNewOpenModeInstanceAndAssignsItToTheContextProvider() {
        Player player = mock(Player.class);
        bukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(player));

        GamePlayer gamePlayer = mock(GamePlayer.class);
        PlayerLifecycleHandler playerLifecycleHandler = mock(PlayerLifecycleHandler.class);
        StatePersistenceHandler statePersistenceHandler = mock(StatePersistenceHandler.class);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.registerEntity(player)).thenReturn(gamePlayer);

        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        when(collisionDetectorProvider.get()).thenReturn(collisionDetector);

        when(playerLifecycleHandlerFactory.create(any(ItemLifecycleHandler.class), eq(playerRegistry), eq(statePersistenceHandler))).thenReturn(playerLifecycleHandler);
        when(playerRegistryFactory.create(any())).thenReturn(playerRegistry);
        when(statePersistenceHandlerFactory.create(any(EquipmentRegistry.class), any(GunRegistry.class), eq(playerRegistry))).thenReturn(statePersistenceHandler);
        when(configuration.isEnabledRegisterPlayersAsPassive()).thenReturn(true);

        OpenModeInitializer openModeInitializer = new OpenModeInitializer(configuration, eventDispatcher, contextProvider, playerRegistryFactory, playerLifecycleHandlerFactory, statePersistenceHandlerFactory, collisionDetectorProvider);
        openModeInitializer.initialize();

        ArgumentCaptor<EntityDamageEventHandler> entityDamageEventHandlerCaptor = ArgumentCaptor.forClass(EntityDamageEventHandler.class);
        verify(eventDispatcher).registerEventHandler(eq(EntityDamageEvent.class), entityDamageEventHandlerCaptor.capture());

        verify(gamePlayer).setPassive(true);
        verify(statePersistenceHandler).loadPlayerState(gamePlayer);
    }
}
