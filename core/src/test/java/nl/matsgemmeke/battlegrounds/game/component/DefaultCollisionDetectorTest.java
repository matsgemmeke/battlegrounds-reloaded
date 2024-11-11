package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultCollisionDetectorTest {

    private BlockCollisionChecker blockCollisionChecker;

    @Before
    public void setUp() {
        blockCollisionChecker = mock(BlockCollisionChecker.class);
    }

    @Test
    public void hasLineOfSightReturnsFalseIfGivenEitherOfGivenTwoLocationsHasNoWorld() {
        Location from = new Location(null, 0, 0, 0);
        Location to = new Location(null, 2, 0, 0);

        DefaultCollisionDetector collisionDetector = new DefaultCollisionDetector(blockCollisionChecker);
        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertFalse(hasLineOfSight);
    }

    @Test
    public void hasLineOfSightReturnsFalseIfGivenTwoLocationsAreNotInSameWorld() {
        Location from = new Location(mock(World.class), 0, 0, 0);
        Location to = new Location(mock(World.class), 2, 0, 0);

        DefaultCollisionDetector collisionDetector = new DefaultCollisionDetector(blockCollisionChecker);
        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertFalse(hasLineOfSight);
    }

    @Test
    public void hasLineOfSightReturnsFalseIfNonPassableBlocksExistBetweenGivenTwoLocations() {
        World world = mock(World.class);

        Location from = new Location(world, 0, 0, 0);
        Location to = new Location(world, 2, 0, 0);

        this.createBlock(world, 0, 0, 0, Material.AIR);
        this.createBlock(world, 1, 0, 0, Material.STONE);
        this.createBlock(world, 2, 0, 0, Material.AIR);

        DefaultCollisionDetector collisionDetector = new DefaultCollisionDetector(blockCollisionChecker);
        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertFalse(hasLineOfSight);
    }

    @Test
    public void hasLineOfSightReturnsTrueIfAllBlocksBetweenGivenTwoLocationsArePassable() {
        World world = mock(World.class);

        Location from = new Location(world, 0, 0, 0);
        Location to = new Location(world, 2, 0, 0);

        this.createBlock(world, 0, 0, 0, Material.AIR);
        this.createBlock(world, 1, 0, 0, Material.AIR);
        this.createBlock(world, 2, 0, 0, Material.AIR);

        DefaultCollisionDetector collisionDetector = new DefaultCollisionDetector(blockCollisionChecker);
        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertTrue(hasLineOfSight);
    }

    @Test
    public void shouldReturnWhetherGivenLocationIsInOpenSegmentOfBlock() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        when(blockCollisionChecker.isSolid(any(), eq(location))).thenReturn(true);

        DefaultCollisionDetector collisionDetector = new DefaultCollisionDetector(blockCollisionChecker);
        boolean collision = collisionDetector.producesBlockCollisionAt(location);

        assertTrue(collision);
    }

    private void createBlock(World world, int x, int y, int z, Material material) {
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(material);
        when(world.getBlockAt(x, y, z)).thenReturn(block);
    }
}
