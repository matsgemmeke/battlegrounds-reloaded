package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.training.TrainingMode;
import org.bukkit.entity.Item;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DefaultTrainingModeContextTest {

    private TrainingMode trainingMode;

    @Before
    public void setUp() {
        trainingMode = mock(TrainingMode.class);
    }

    @Test
    public void shouldReturnNewInstanceOfAudioEmitter() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode);

        AudioEmitter audioEmitter = context.getAudioEmitter();

        assertTrue(audioEmitter instanceof DefaultAudioEmitter);
    }

    @Test
    public void shouldReturnNewInstanceOfCollisionDetector() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode);

        CollisionDetector collisionDetector = context.getCollisionDetector();

        assertTrue(collisionDetector instanceof TrainingModeCollisionDetector);
    }

    @Test
    public void shouldReturnNewInstanceOfEntityRegister() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode);

        EntityRegistry<Item, GameItem> itemRegistry = context.getItemRegistry();

        assertTrue(itemRegistry instanceof DefaultItemRegistry);
    }
}
