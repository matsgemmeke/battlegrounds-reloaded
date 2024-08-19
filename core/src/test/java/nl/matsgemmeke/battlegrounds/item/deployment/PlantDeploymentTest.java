package nl.matsgemmeke.battlegrounds.item.deployment;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlantDeploymentTest {

    private Material material = Material.WARPED_BUTTON;

    @Test
    public void shouldUpdateBlockDataOfWhenPlantingAgainstWall() {
        BlockFace blockFace = BlockFace.NORTH;
        BlockState blockState = mock(BlockState.class);
        Directional directional = mock(Directional.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);

        Block block = mock(Block.class);
        when(block.getBlockData()).thenReturn(faceAttachable).thenReturn(directional);
        when(block.getState()).thenReturn(blockState);

        PlantDeployment deployment = new PlantDeployment(material);
        deployment.plant(block, blockFace);

        verify(directional).setFacing(blockFace);
        verify(blockState).setBlockData(directional);
        verify(faceAttachable).setAttachedFace(AttachedFace.WALL);
        verify(blockState).setBlockData(faceAttachable);
    }

    @Test
    public void shouldUpdateBlockDataOfWhenPlantingAgainstCeiling() {
        BlockFace blockFace = BlockFace.DOWN;
        BlockState blockState = mock(BlockState.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);

        Block block = mock(Block.class);
        when(block.getBlockData()).thenReturn(faceAttachable);
        when(block.getState()).thenReturn(blockState);

        PlantDeployment deployment = new PlantDeployment(material);
        deployment.plant(block, blockFace);

        verify(faceAttachable).setAttachedFace(AttachedFace.CEILING);
        verify(blockState).setBlockData(faceAttachable);
    }

    @Test
    public void shouldUpdateBlockDataOfWhenPlantingAgainstFloor() {
        BlockFace blockFace = BlockFace.UP;
        BlockState blockState = mock(BlockState.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);

        Block block = mock(Block.class);
        when(block.getBlockData()).thenReturn(faceAttachable);
        when(block.getState()).thenReturn(blockState);

        PlantDeployment deployment = new PlantDeployment(material);
        deployment.plant(block, blockFace);

        verify(faceAttachable).setAttachedFace(AttachedFace.FLOOR);
        verify(blockState).setBlockData(faceAttachable);
    }
}
