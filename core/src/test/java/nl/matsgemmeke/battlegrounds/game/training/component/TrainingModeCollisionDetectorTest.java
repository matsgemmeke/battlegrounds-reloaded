package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingModeCollisionDetectorTest {

    private BlockCollisionChecker blockCollisionChecker;

    @Before
    public void setUp() {
        blockCollisionChecker = mock(BlockCollisionChecker.class);
    }

    @Test
    public void shouldReturnWhetherGivenLocationIsInOpenSegmentOfBlock() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        when(blockCollisionChecker.isSolid(any(), eq(location))).thenReturn(true);

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker);
        boolean collision = collisionDetector.producesBlockCollisionAt(location);

        assertTrue(collision);
    }
}
