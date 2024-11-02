package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.PlacedBlock;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
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
    private ItemEffectActivation effectActivation;
    private long delayAfterPlacement;
    private Material material;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        effectActivation = mock(ItemEffectActivation.class);
        delayAfterPlacement = 10L;
        material = Material.WARPED_BUTTON;
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldNotBePerformingIfNoBlocksWerePlaced() {
        PlaceFunction function = new PlaceFunction(effectActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
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

        PlaceFunction function = new PlaceFunction(effectActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        function.perform(holder);

        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldNotPerformIfHolderDoesNotReturnTwoTargetBlocks() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(Collections.emptyList());

        PlaceFunction function = new PlaceFunction(effectActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(effectActivation);
    }

    @Test
    public void shouldNotPerformIfHolderIsTargetingOccludingBlock() {
        Block targetBlock = mock(Block.class);
        when(targetBlock.getType()).thenReturn(Material.OAK_FENCE);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(targetBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(effectActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(effectActivation);
    }

    @Test
    public void shouldNotPerformIfAdjacentBlockIsNotConnectedToTargetBlock() {
        Block adjacentBlock = mock(Block.class);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(null);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(effectActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(effectActivation);
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

        PlaceFunction function = new PlaceFunction(effectActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        function.addSounds(sounds);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlacedBlock> captor = ArgumentCaptor.forClass(PlacedBlock.class);
        verify(effectActivation).prime(eq(holder), captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(audioEmitter).playSounds(any(), eq(location));
        verify(faceAttachable).setAttachedFace(AttachedFace.CEILING);
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

        PlaceFunction function = new PlaceFunction(effectActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        function.addSounds(sounds);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlacedBlock> captor = ArgumentCaptor.forClass(PlacedBlock.class);
        verify(effectActivation).prime(eq(holder), captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(audioEmitter).playSounds(any(), eq(location));
        verify(faceAttachable).setAttachedFace(AttachedFace.FLOOR);
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

        when(effectActivation.isPrimed()).thenReturn(true);

        PlaceFunction function = new PlaceFunction(effectActivation, material, audioEmitter, taskRunner, delayAfterPlacement);
        function.addSounds(sounds);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlacedBlock> captor = ArgumentCaptor.forClass(PlacedBlock.class);
        verify(effectActivation).prime(eq(holder), captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(adjacentBlockState).setBlockData(directional);
        verify(audioEmitter).playSounds(any(), eq(location));
        verify(directional).setFacing(targetBlockFace);
        verify(faceAttachable).setAttachedFace(AttachedFace.WALL);
    }
}
