package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.DefaultPlayerRegistryFactory;
import nl.matsgemmeke.battlegrounds.game.component.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.event.EntityDamageEventHandler;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
import nl.matsgemmeke.battlegrounds.game.training.component.spawn.TrainingModeSpawnPointProviderFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

public class TrainingModeContextProviderTest {

    private BattlegroundsConfiguration configuration;
    private EventDispatcher eventDispatcher;
    private GameContextProvider contextProvider;
    private DefaultPlayerRegistryFactory playerRegistryFactory;
    private TrainingModeSpawnPointProviderFactory spawnPointProviderFactory;
    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    public void setUp() {
        configuration = mock(BattlegroundsConfiguration.class);
        eventDispatcher = mock(EventDispatcher.class);
        contextProvider = mock(GameContextProvider.class);
        playerRegistryFactory = mock(DefaultPlayerRegistryFactory.class);
        spawnPointProviderFactory = mock(TrainingModeSpawnPointProviderFactory.class);
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

        when(playerRegistryFactory.make(any())).thenReturn(playerRegistry);

        SpawnPointProvider spawnPointProvider = mock(SpawnPointProvider.class);
        when(spawnPointProviderFactory.make(any(SpawnPointStorage.class))).thenReturn(spawnPointProvider);

        when(configuration.isEnabledRegisterPlayersAsPassive()).thenReturn(true);

        TrainingModeContextProvider provider = new TrainingModeContextProvider(configuration, eventDispatcher, contextProvider, playerRegistryFactory, spawnPointProviderFactory);
        GameContext context = provider.get();

        ArgumentCaptor<EntityDamageEventHandler> entityDamageEventHandlerCaptor = ArgumentCaptor.forClass(EntityDamageEventHandler.class);
        verify(eventDispatcher).registerEventHandler(eq(EntityDamageEvent.class), entityDamageEventHandlerCaptor.capture());

        assertInstanceOf(TrainingModeContext.class, context);

        verify(contextProvider).assignTrainingModeContext(context);
        verify(gamePlayer).setPassive(true);
    }
}
