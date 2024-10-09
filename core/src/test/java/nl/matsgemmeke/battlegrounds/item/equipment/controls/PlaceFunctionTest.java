package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.deployment.DeployableSource;
import nl.matsgemmeke.battlegrounds.item.deployment.PlacedBlock;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlaceFunctionTest {

    private AudioEmitter audioEmitter;
    private DeployableSource item;
    private ItemMechanismActivation mechanismActivation;
    private long delayAfterPlacement;
    private Material material;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        item = mock(DeployableSource.class);
        mechanismActivation = mock(ItemMechanismActivation.class);
        delayAfterPlacement = 10L;
        material = Material.WARPED_BUTTON;
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldBeAvailableIfItemHasNoDeployedObjectsYet() {
        when(item.getDeployedObjects()).thenReturn(Collections.emptyList());

        PlaceFunction function = new PlaceFunction(item, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void shouldNotBeAvailableIfItemHasDeployedObjects() {
        when(item.getDeployedObjects()).thenReturn(List.of(mock(Deployable.class)));

        PlaceFunction function = new PlaceFunction(item, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldNotBePerformingIfNoBlocksWerePlaced() {
        PlaceFunction function = new PlaceFunction(item, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        boolean performing = function.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void shouldBePerformingIfBlockWasRecentlyPlaced() {
        BlockFace targetBlockFace = BlockFace.DOWN;
        BlockState adjacentBlockState = mock(BlockState.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        Location location = new Location(null, 1, 1, 1);

        Block adjacentBlock = mock(Block.class);
        when(adjacentBlock.getBlockData()).thenReturn(faceAttachable);
        when(adjacentBlock.getLocation()).thenReturn(location);
        when(adjacentBlock.getState()).thenReturn(adjacentBlockState);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(item, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        function.perform(holder);

        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldNotPerformIfHolderDoesNotReturnTwoTargetBlocks() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(Collections.emptyList());

        PlaceFunction function = new PlaceFunction(item, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(item);
        verifyNoInteractions(mechanismActivation);
    }

    @Test
    public void shouldNotPerformIfHolderIsTargetingOccludingBlock() {
        Block targetBlock = mock(Block.class);
        when(targetBlock.getType()).thenReturn(Material.OAK_FENCE);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(targetBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(item, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(item);
        verifyNoInteractions(mechanismActivation);
    }

    @Test
    public void shouldNotPerformIfAdjacentBlockIsNotConnectedToTargetBlock() {
        Block adjacentBlock = mock(Block.class);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(null);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(item, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(item);
        verifyNoInteractions(mechanismActivation);
    }

    @Test
    public void shouldPlaceBlockAgainstCeilingWhenPerforming() {
        BlockFace targetBlockFace = BlockFace.DOWN;
        BlockState adjacentBlockState = mock(BlockState.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        Location location = new Location(null, 1, 1, 1);

        Block adjacentBlock = mock(Block.class);
        when(adjacentBlock.getBlockData()).thenReturn(faceAttachable);
        when(adjacentBlock.getLocation()).thenReturn(location);
        when(adjacentBlock.getState()).thenReturn(adjacentBlockState);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        List<GameSound> sounds = List.of(mock(GameSound.class));

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        when(mechanismActivation.isPrimed()).thenReturn(false);

        PlaceFunction function = new PlaceFunction(item, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        function.addSounds(sounds);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlacedBlock> captor = ArgumentCaptor.forClass(PlacedBlock.class);
        verify(item).onDeploy(captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(audioEmitter).playSounds(any(), eq(location));
        verify(faceAttachable).setAttachedFace(AttachedFace.CEILING);
        verify(mechanismActivation).primeDeployedObject(holder, captor.getValue());
    }

    @Test
    public void shouldPlaceBlockAgainstFloorWhenPerforming() {
        BlockFace targetBlockFace = BlockFace.UP;
        BlockState adjacentBlockState = mock(BlockState.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        Location location = new Location(null, 1, 1, 1);

        Block adjacentBlock = mock(Block.class);
        when(adjacentBlock.getBlockData()).thenReturn(faceAttachable);
        when(adjacentBlock.getLocation()).thenReturn(location);
        when(adjacentBlock.getState()).thenReturn(adjacentBlockState);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        List<GameSound> sounds = List.of(mock(GameSound.class));

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        when(mechanismActivation.isPrimed()).thenReturn(true);

        PlaceFunction function = new PlaceFunction(item, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        function.addSounds(sounds);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlacedBlock> captor = ArgumentCaptor.forClass(PlacedBlock.class);
        verify(item).onDeploy(captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(audioEmitter).playSounds(any(), eq(location));
        verify(faceAttachable).setAttachedFace(AttachedFace.FLOOR);
        verify(mechanismActivation).deploy(captor.getValue());
    }

    @Test
    public void shouldPlaceBlockAgainstWallWhenPerforming() {
        BlockFace targetBlockFace = BlockFace.NORTH;
        BlockState adjacentBlockState = mock(BlockState.class);
        Directional directional = mock(Directional.class);
        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        Location location = new Location(null, 1, 1, 1);

        Block adjacentBlock = mock(Block.class);
        when(adjacentBlock.getBlockData()).thenReturn(faceAttachable).thenReturn(directional);
        when(adjacentBlock.getLocation()).thenReturn(location);
        when(adjacentBlock.getState()).thenReturn(adjacentBlockState);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        List<GameSound> sounds = List.of(mock(GameSound.class));

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        when(mechanismActivation.isPrimed()).thenReturn(true);

        PlaceFunction function = new PlaceFunction(item, mechanismActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        function.addSounds(sounds);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlacedBlock> captor = ArgumentCaptor.forClass(PlacedBlock.class);
        verify(item).onDeploy(captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(adjacentBlockState).setBlockData(directional);
        verify(audioEmitter).playSounds(any(), eq(location));
        verify(directional).setFacing(targetBlockFace);
        verify(faceAttachable).setAttachedFace(AttachedFace.WALL);
        verify(mechanismActivation).deploy(captor.getValue());
    }
}
