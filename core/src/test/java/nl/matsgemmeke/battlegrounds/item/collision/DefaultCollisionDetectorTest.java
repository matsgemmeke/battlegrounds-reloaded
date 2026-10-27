package nl.matsgemmeke.battlegrounds.item.collision;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollisionDetectorTest {

    @Mock
    private BlockCollisionChecker blockCollisionChecker;
    @InjectMocks
    private CollisionDetector collisionDetector;

    @Test
    @DisplayName("hasLineOfSight returns false when given from location has no world")
    void hasLineOfSight_fromNoWorld() {
        Location from = new Location(null, 0, 0, 0);
        Location to = new Location(mock(World.class), 2, 0, 0);

        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertThat(hasLineOfSight).isFalse();
    }

    @Test
    @DisplayName("hasLineOfSight returns false when given to location has no world")
    void hasLineOfSight_toNoWorld() {
        Location from = new Location(mock(World.class), 0, 0, 0);
        Location to = new Location(null, 2, 0, 0);

        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertThat(hasLineOfSight).isFalse();
    }

    @Test
    @DisplayName("hasLineOfSight returns false when given two locations are not in the same world")
    void hasLineOfSight_differentWorlds() {
        Location from = new Location(mock(World.class), 0, 0, 0);
        Location to = new Location(mock(World.class), 2, 0, 0);

        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertThat(hasLineOfSight).isFalse();
    }

    @Test
    @DisplayName("hasLineOfSight returns false when non passable blocks exist between given two locations")
    void hasLineOfSight_nonPassableBlockInBetween() {
        World world = mock(World.class);
        Location from = new Location(world, 0, 0, 0);
        Location to = new Location(world, 2, 0, 0);

        this.createBlock(world, 0, 0, 0, Material.AIR);
        this.createBlock(world, 1, 0, 0, Material.STONE);
        this.createBlock(world, 2, 0, 0, Material.AIR);

        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertThat(hasLineOfSight).isFalse();
    }

    @Test
    @DisplayName("hasLineOfSight returns true when all blocks between given two locations are passable")
    void hasLineOfSight_allPassibleBlocksInBetween() {
        World world = mock(World.class);

        Location from = new Location(world, 0, 0, 0);
        Location to = new Location(world, 2, 0, 0);

        this.createBlock(world, 0, 0, 0, Material.AIR);
        this.createBlock(world, 1, 0, 0, Material.AIR);
        this.createBlock(world, 2, 0, 0, Material.AIR);

        boolean hasLineOfSight = collisionDetector.hasLineOfSight(from, to);

        assertThat(hasLineOfSight).isTrue();
    }

    @Test
    @DisplayName("producesBlockCollisionAt returns true when given location is in open segment of block")
    void producesBlockCollisionAt_locationInOpenSegment() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        when(blockCollisionChecker.isSolid(any(), eq(location))).thenReturn(true);

        boolean collision = collisionDetector.producesBlockCollisionAt(location);

        assertThat(collision).isTrue();
    }

    private void createBlock(World world, int x, int y, int z, Material material) {
        Block block = mock(Block.class);
        lenient().when(block.getType()).thenReturn(material);
        lenient().when(world.getBlockAt(x, y, z)).thenReturn(block);
    }
}
