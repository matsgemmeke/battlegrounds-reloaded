package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.component.EntityFinder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingModeCollisionDetectorTest {

    private BlockCollisionChecker blockCollisionChecker;
    private EntityFinder entityFinder;

    @Before
    public void setUp() {
        blockCollisionChecker = mock(BlockCollisionChecker.class);
        entityFinder = mock(EntityFinder.class);
    }

    @Test
    public void shouldReturnNoTargetsIfGivenLocationHasNoWorld() {
        GameEntity entity = mock(GameEntity.class);
        Location location = new Location(null, 1.0, 1.0, 1.0);

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker, entityFinder);
        Collection<GameEntity> targets = collisionDetector.findTargets(entity, location, 0.1);

        assertTrue(targets.isEmpty());
    }

    @Test
    public void shouldReturnListOfTargetsBasedOnNearbyEntities() {
        double range = 0.1;

        GameEntity entity = mock(GameEntity.class);
        GamePlayer target = mock(GamePlayer.class);
        Player targetEntity = mock(Player.class);
        World world = mock(World.class);

        Location location = new Location(world, 1.0, 1.0, 1.0);

        when(entityFinder.findEntity(targetEntity)).thenReturn(target);
        when(world.getNearbyEntities(location, range, range, range)).thenReturn(Collections.singletonList(targetEntity));

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker, entityFinder);
        Collection<GameEntity> targets = collisionDetector.findTargets(entity, location, range);

        assertEquals(1, targets.size());
        assertEquals(target, targets.iterator().next());
    }

    @Test
    public void shouldReturnWhetherGivenLocationIsInOpenSegmentOfBlock() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        when(blockCollisionChecker.isSolid(any(), eq(location))).thenReturn(true);

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker, entityFinder);
        boolean collision = collisionDetector.producesBlockCollisionAt(location);

        assertTrue(collision);
    }
}
