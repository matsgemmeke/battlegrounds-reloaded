package nl.matsgemmeke.battlegrounds.item.collision;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollisionDetectorTest {

    @Mock
    private BlockCollisionChecker blockCollisionChecker;
    @InjectMocks
    private CollisionDetector collisionDetector;

    @Test
    public void hasLineOfSightReturnsFalseIfGivenEitherOfGivenTwoLocationsHasNoWorld() {
        Location from = new Location(null, 0, 0, 0);
        Location to = new Location(null, 2, 0, 0);

        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertFalse(hasLineOfSight);
    }

    @Test
    public void hasLineOfSightReturnsFalseIfGivenTwoLocationsAreNotInSameWorld() {
        Location from = new Location(mock(World.class), 0, 0, 0);
        Location to = new Location(mock(World.class), 2, 0, 0);

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

        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertTrue(hasLineOfSight);
    }

    @Test
    public void shouldReturnWhetherGivenLocationIsInOpenSegmentOfBlock() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        when(blockCollisionChecker.isSolid(any(), eq(location))).thenReturn(true);

        boolean collision = collisionDetector.producesBlockCollisionAt(location);

        assertTrue(collision);
    }

    private void createBlock(World world, int x, int y, int z, Material material) {
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(material);
        when(world.getBlockAt(x, y, z)).thenReturn(block);
    }
}
