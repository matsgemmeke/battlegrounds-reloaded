package nl.matsgemmeke.battlegrounds.game.training;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.entity.DefaultPlayerRegistryFactory;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TrainingModeGameKeyProviderTest {

    private BattlegroundsConfiguration configuration;
    private EventDispatcher eventDispatcher;
    private GameContextProvider contextProvider;
    private DefaultPlayerRegistryFactory playerRegistryFactory;
    private Provider<CollisionDetector> collisionDetectorProvider;
    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    public void setUp() {
        configuration = mock(BattlegroundsConfiguration.class);
        eventDispatcher = mock(EventDispatcher.class);
        contextProvider = new GameContextProvider();
        playerRegistryFactory = mock(DefaultPlayerRegistryFactory.class);
        collisionDetectorProvider = mock();
        bukkit = mockStatic(Bukkit.class);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void getCreatesNewTrainingModeContextAndAssignsItToTheContextProvider() {
        Player player = mock(Player.class);
        bukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(player));

        GamePlayer gamePlayer = mock(GamePlayer.class);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.registerEntity(player)).thenReturn(gamePlayer);

        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        when(collisionDetectorProvider.get()).thenReturn(collisionDetector);

        when(playerRegistryFactory.make(any())).thenReturn(playerRegistry);
        when(configuration.isEnabledRegisterPlayersAsPassive()).thenReturn(true);

        TrainingModeGameKeyProvider provider = new TrainingModeGameKeyProvider(configuration, eventDispatcher, contextProvider, playerRegistryFactory, collisionDetectorProvider);
        GameKey gameKey = provider.get();

        ArgumentCaptor<EntityDamageEventHandler> entityDamageEventHandlerCaptor = ArgumentCaptor.forClass(EntityDamageEventHandler.class);
        verify(eventDispatcher).registerEventHandler(eq(EntityDamageEvent.class), entityDamageEventHandlerCaptor.capture());

        assertEquals("TRAINING-MODE", gameKey.toString());

        verify(gamePlayer).setPassive(true);
    }
}
