package nl.matsgemmeke.battlegrounds.item.deployment;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class PlantableObjectTest {

    private Block block;
    private BlockFace blockFace;
    private BlockState blockState;
    private Material material;

    @Before
    public void setUp() {
        blockFace = BlockFace.NORTH;
        blockState = mock(BlockState.class);
        material = Material.WARPED_BUTTON;

        block = mock(Block.class);
        when(block.getState()).thenReturn(blockState);
    }

    @Test
    public void shouldReturnSameLocationAsBlockWhereObjectIsPlantedOn() {
        Location location = new Location(null, 1, 1, 1);
        when(block.getLocation()).thenReturn(location);

        PlantableObject plantableObject = new PlantableObject(block, blockFace, material);
        Location objectLocation = plantableObject.getLocation();

        assertEquals(location, objectLocation);
    }

    @Test
    public void shouldReturnSameWorldAsBlockWhereObjectIsPlantedOn() {
        World world = mock(World.class);
        when(block.getWorld()).thenReturn(world);

        PlantableObject plantableObject = new PlantableObject(block, blockFace, material);
        World objectWorld = plantableObject.getWorld();

        assertEquals(world, objectWorld);
    }

    @Test
    public void shouldUpdateBlockDataOfWhenPlantingAgainstWall() {
        Directional directional = mock(Directional.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        when(block.getBlockData()).thenReturn(faceAttachable).thenReturn(directional);

        PlantableObject plantableObject = new PlantableObject(block, blockFace, material);
        plantableObject.plant();

        verify(directional).setFacing(blockFace);
        verify(blockState).setBlockData(directional);
        verify(faceAttachable).setAttachedFace(AttachedFace.WALL);
        verify(blockState).setBlockData(faceAttachable);
    }

    @Test
    public void shouldUpdateBlockDataOfWhenPlantingAgainstCeiling() {
        BlockFace blockFace = BlockFace.DOWN;

        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        when(block.getBlockData()).thenReturn(faceAttachable);

        PlantableObject plantableObject = new PlantableObject(block, blockFace, material);
        plantableObject.plant();

        verify(faceAttachable).setAttachedFace(AttachedFace.CEILING);
        verify(blockState).setBlockData(faceAttachable);
    }

    @Test
    public void shouldUpdateBlockDataOfWhenPlantingAgainstFloor() {
        BlockFace blockFace = BlockFace.UP;

        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        when(block.getBlockData()).thenReturn(faceAttachable);

        PlantableObject plantableObject = new PlantableObject(block, blockFace, material);
        plantableObject.plant();

        verify(faceAttachable).setAttachedFace(AttachedFace.FLOOR);
        verify(blockState).setBlockData(faceAttachable);
    }

    @Test
    public void shouldRemoveBlockWhenRemovingObject() {
        PlantableObject plantableObject = new PlantableObject(block, blockFace, material);
        plantableObject.remove();

        verify(block).setType(Material.AIR);
    }
}
