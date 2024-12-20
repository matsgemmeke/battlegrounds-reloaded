package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunctionException;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.PlacedBlock;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlaceFunctionTest {

    private static final Iterable<GameSound> PLACE_SOUNDS = Collections.emptySet();
    private static final long DELAY_AFTER_PLACEMENT = 10L;
    private static final Material MATERIAL = Material.WARPED_BUTTON;

    private AudioEmitter audioEmitter;
    private Equipment equipment;
    private PlaceProperties properties;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        equipment = mock(Equipment.class);
        properties = new PlaceProperties(PLACE_SOUNDS, MATERIAL, DELAY_AFTER_PLACEMENT);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void isPerformingReturnsFalseIfNoBlocksWerePlaced() {
        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performing = function.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void isPerformingReturnsTrueIfBlockWasRecentlyPlaced() {
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

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(equipment.getEffectActivation()).thenReturn(effectActivation);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        function.perform(holder);

        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void performThrowsExceptionIfEquipmentHasNoEffectActivation() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        when(equipment.getEffectActivation()).thenReturn(null);

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);

        assertThrows(ItemFunctionException.class, () -> function.perform(holder));
    }

    @Test
    public void performReturnsFalseIfHolderDoesNotReturnTwoTargetBlocks() {
        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(equipment.getEffectActivation()).thenReturn(effectActivation);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(Collections.emptyList());

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(effectActivation);
    }

    @Test
    public void performReturnsFalseIfHolderIsTargetingOccludingBlock() {
        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(equipment.getEffectActivation()).thenReturn(effectActivation);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getType()).thenReturn(Material.OAK_FENCE);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(targetBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(effectActivation);
    }

    @Test
    public void performReturnsFalseIfAdjacentBlockIsNotConnectedToTargetBlock() {
        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(equipment.getEffectActivation()).thenReturn(effectActivation);

        Block adjacentBlock = mock(Block.class);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(null);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(effectActivation);
    }

    @Test
    public void performReturnsTrueWhenPlacingBlockAgainstCeiling() {
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

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(equipment.getEffectActivation()).thenReturn(effectActivation);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlacedBlock> captor = ArgumentCaptor.forClass(PlacedBlock.class);
        verify(effectActivation).prime(eq(holder), captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(audioEmitter).playSounds(PLACE_SOUNDS, location);
        verify(faceAttachable).setAttachedFace(AttachedFace.CEILING);
    }

    @Test
    public void performReturnsTrueWhenPlacingBlockAgainstFloor() {
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

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(equipment.getEffectActivation()).thenReturn(effectActivation);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
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

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(equipment.getEffectActivation()).thenReturn(effectActivation);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
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
