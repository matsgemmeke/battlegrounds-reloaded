package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    public void shouldReturnNoTargetsIfGivenLocationHasNoWorld() {
        GameEntity entity = mock(GameEntity.class);
        Location location = new Location(null, 1.0, 1.0, 1.0);

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker);
        Collection<GameEntity> targets = collisionDetector.findTargets(entity, location, 0.1);

        assertTrue(targets.isEmpty());
    }

    @Test
    public void shouldReturnListOfTargetsBasedOnNearbyEntities() {
        double range = 0.1;

        GameEntity entity = mock(GameEntity.class);
        World world = mock(World.class);

        List<Entity> nearbyEntities = Collections.singletonList(mock(Player.class));
        Location location = new Location(world, 1.0, 1.0, 1.0);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(nearbyEntities);

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker);
        Collection<GameEntity> targets = collisionDetector.findTargets(entity, location, range);

        assertEquals(1, targets.size());
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
