package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

public class TrainingModeContextProviderTest {

    private GameContextProvider contextProvider;
    private InternalsProvider internals;
    private TrainingModeFactory trainingModeFactory;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        internals = mock(InternalsProvider.class);
        trainingModeFactory = mock(TrainingModeFactory.class);
    }

    @Test
    public void getCreatesNewTrainingModeContextAndAssignsItToTheContextProvider() {
        TrainingMode trainingMode = new TrainingMode(internals, new ItemStorage<>(), new ItemStorage<>());
        when(trainingModeFactory.make()).thenReturn(trainingMode);

        TrainingModeContextProvider provider = new TrainingModeContextProvider(contextProvider, internals, trainingModeFactory);
        GameContext context = provider.get();

        assertInstanceOf(TrainingModeContext.class, context);

        verify(contextProvider).assignTrainingModeContext(context);
    }
}
