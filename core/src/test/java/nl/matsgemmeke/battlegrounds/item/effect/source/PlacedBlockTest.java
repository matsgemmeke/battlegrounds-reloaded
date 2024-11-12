package nl.matsgemmeke.battlegrounds.item.effect.source;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PlacedBlockTest {

    private Block block;
    private Material material;

    @Before
    public void setUp() {
        block = mock(Block.class);
        material = Material.WARPED_BUTTON;
    }

    @Test
    public void shouldExistIfBlockTypeEqualsOriginalMaterial() {
        when(block.getType()).thenReturn(material);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        boolean exists = placedBlock.exists();

        assertTrue(exists);
    }

    @Test
    public void shouldNotExistIfBlockTypeDoesNotEqualOriginalMaterial() {
        when(block.getType()).thenReturn(Material.STONE);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        boolean exists = placedBlock.exists();

        assertFalse(exists);
    }

    @Test
    public void shouldReturnSameLocationAsBlockWhereObjectIsPlacedOn() {
        Location location = new Location(null, 1, 1, 1);
        when(block.getLocation()).thenReturn(location);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        Location objectLocation = placedBlock.getLocation();

        assertEquals(location, objectLocation);
    }

    @Test
    public void shouldReturnSameWorldAsBlockWhereObjectIsPlacedOn() {
        World world = mock(World.class);
        when(block.getWorld()).thenReturn(world);

        PlacedBlock placedBlock = new PlacedBlock(block, material);
        World objectWorld = placedBlock.getWorld();

        assertEquals(world, objectWorld);
    }

    @Test
    public void isDeployedAlwaysReturnsTrue() {
        PlacedBlock placedBlock = new PlacedBlock(block, material);
        boolean deployed = placedBlock.isDeployed();

        assertTrue(deployed);
    }

    @Test
    public void shouldRemoveBlockWhenRemovingObject() {
        PlacedBlock placedBlock = new PlacedBlock(block, material);
        placedBlock.remove();

        verify(block).setType(Material.AIR);
    }
}
