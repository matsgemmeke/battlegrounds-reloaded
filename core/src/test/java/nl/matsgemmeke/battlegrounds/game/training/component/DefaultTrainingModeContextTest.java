package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DefaultTrainingModeContextTest {

    @Test
    public void shouldReturnNewInstanceOfCollisionDetector() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext();

        CollisionDetector collisionDetector = context.getCollisionDetector();

        assertTrue(collisionDetector instanceof TrainingModeCollisionDetector);
    }
}
