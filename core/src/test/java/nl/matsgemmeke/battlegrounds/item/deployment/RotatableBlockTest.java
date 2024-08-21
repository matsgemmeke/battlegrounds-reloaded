package nl.matsgemmeke.battlegrounds.item.deployment;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class RotatableBlockTest {

    private Block block;

    @Before
    public void setUp() {
        block = mock(Block.class);
    }

    @Test
    public void shouldReturnSameLocationAsBlockWhereObjectIsPlacedOn() {
        Location location = new Location(null, 1, 1, 1);
        when(block.getLocation()).thenReturn(location);

        RotatableBlock rotatableBlock = new RotatableBlock(block);
        Location objectLocation = rotatableBlock.getLocation();

        assertEquals(location, objectLocation);
    }

    @Test
    public void shouldReturnSameWorldAsBlockWhereObjectIsPlacedOn() {
        World world = mock(World.class);
        when(block.getWorld()).thenReturn(world);

        RotatableBlock rotatableBlock = new RotatableBlock(block);
        World objectWorld = rotatableBlock.getWorld();

        assertEquals(world, objectWorld);
    }

    @Test
    public void shouldRemoveBlockWhenRemovingObject() {
        RotatableBlock rotatableBlock = new RotatableBlock(block);
        rotatableBlock.remove();

        verify(block).setType(Material.AIR);
    }
}
