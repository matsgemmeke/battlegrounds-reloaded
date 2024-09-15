package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class TrainingModeFactoryTest {

    private InternalsProvider internals;

    @Before
    public void setUp() {
        internals = mock(InternalsProvider.class);
    }

    @Test
    public void createNewInstanceOfTrainingMode() {
        TrainingModeFactory factory = new TrainingModeFactory(internals);
        TrainingMode trainingMode = factory.make();

        assertTrue(trainingMode instanceof DefaultTrainingMode);
    }
}
