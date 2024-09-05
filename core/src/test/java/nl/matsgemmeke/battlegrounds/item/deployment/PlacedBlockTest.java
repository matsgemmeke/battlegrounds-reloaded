package nl.matsgemmeke.battlegrounds.item.deployment;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class PlacedBlockTest {

    private Block block;

    @Before
    public void setUp() {
        block = mock(Block.class);
    }

    @Test
    public void shouldReturnSameLocationAsBlockWhereObjectIsPlacedOn() {
        Location location = new Location(null, 1, 1, 1);
        when(block.getLocation()).thenReturn(location);

        PlacedBlock placedBlock = new PlacedBlock(block);
        Location objectLocation = placedBlock.getLocation();

        assertEquals(location, objectLocation);
    }

    @Test
    public void shouldReturnSameWorldAsBlockWhereObjectIsPlacedOn() {
        World world = mock(World.class);
        when(block.getWorld()).thenReturn(world);

        PlacedBlock placedBlock = new PlacedBlock(block);
        World objectWorld = placedBlock.getWorld();

        assertEquals(world, objectWorld);
    }

    @Test
    public void shouldRemoveBlockWhenRemovingObject() {
        PlacedBlock placedBlock = new PlacedBlock(block);
        placedBlock.remove();

        verify(block).setType(Material.AIR);
    }
}
