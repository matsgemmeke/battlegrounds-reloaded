package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.DefaultPlayerRegistryFactory;
import nl.matsgemmeke.battlegrounds.game.training.component.spawn.TrainingModeSpawnPointProviderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

public class TrainingModeContextProviderTest {

    private BattlegroundsConfiguration configuration;
    private EventDispatcher eventDispatcher;
    private GameContextProvider contextProvider;
    private DefaultPlayerRegistryFactory playerRegistryFactory;
    private TrainingModeSpawnPointProviderFactory spawnPointProviderFactory;

    @BeforeEach
    public void setUp() {
        configuration = mock(BattlegroundsConfiguration.class);
        eventDispatcher = mock(EventDispatcher.class);
        contextProvider = mock(GameContextProvider.class);
        playerRegistryFactory = mock(DefaultPlayerRegistryFactory.class);
        spawnPointProviderFactory = mock(TrainingModeSpawnPointProviderFactory.class);
    }

    @Test
    public void getCreatesNewTrainingModeContextAndAssignsItToTheContextProvider() {
        TrainingModeContextProvider provider = new TrainingModeContextProvider(configuration, eventDispatcher, contextProvider, playerRegistryFactory, spawnPointProviderFactory);
        GameContext context = provider.get();

        assertInstanceOf(TrainingModeContext.class, context);

        verify(contextProvider).assignTrainingModeContext(context);
    }
}
