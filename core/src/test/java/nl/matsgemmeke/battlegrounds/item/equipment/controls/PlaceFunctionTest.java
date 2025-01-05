package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunctionException;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.PlacedBlock;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
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
import java.util.Map;

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

        ItemEffect effect = mock(ItemEffect.class);
        when(equipment.getEffect()).thenReturn(effect);

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

        when(equipment.getEffect()).thenReturn(null);

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);

        assertThrows(ItemFunctionException.class, () -> function.perform(holder));
    }

    @Test
    public void performReturnsFalseIfHolderDoesNotReturnTwoTargetBlocks() {
        ItemEffect effect = mock(ItemEffect.class);
        when(equipment.getEffect()).thenReturn(effect);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(Collections.emptyList());

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(effect);
    }

    @Test
    public void performReturnsFalseIfHolderIsTargetingOccludingBlock() {
        ItemEffect effect = mock(ItemEffect.class);
        when(equipment.getEffect()).thenReturn(effect);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getType()).thenReturn(Material.OAK_FENCE);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(targetBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(effect);
    }

    @Test
    public void performReturnsFalseIfAdjacentBlockIsNotConnectedToTargetBlock() {
        ItemEffect effect = mock(ItemEffect.class);
        when(equipment.getEffect()).thenReturn(effect);

        Block adjacentBlock = mock(Block.class);

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(null);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(effect);
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

        ItemEffect effect = mock(ItemEffect.class);
        when(effect.isPrimed()).thenReturn(false);

        when(equipment.getEffect()).thenReturn(effect);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlacedBlock> placedBlockCaptor = ArgumentCaptor.forClass(PlacedBlock.class);
        verify(equipment).onDeployDeploymentObject(placedBlockCaptor.capture());

        ArgumentCaptor<ItemEffectContext> contextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(effect).prime(contextCaptor.capture());

        PlacedBlock placedBlock = placedBlockCaptor.getValue();
        ItemEffectContext context = contextCaptor.getValue();

        assertEquals(location, placedBlock.getLocation());

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(audioEmitter).playSounds(PLACE_SOUNDS, location);
        verify(effect).prime(context);
        verify(faceAttachable).setAttachedFace(AttachedFace.CEILING);
        verify(holder).setHeldItem(null);
    }

    @Test
    public void performReturnsTrueWhenDeployingBlockAgainstFloor() {
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

        ItemEffect effect = mock(ItemEffect.class);
        when(effect.isPrimed()).thenReturn(true);

        when(equipment.getEffect()).thenReturn(effect);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlacedBlock> placedBlockCaptor = ArgumentCaptor.forClass(PlacedBlock.class);
        verify(equipment).onDeployDeploymentObject(placedBlockCaptor.capture());

        PlacedBlock placedBlock = placedBlockCaptor.getValue();

        assertEquals(location, placedBlock.getLocation());

        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(audioEmitter).playSounds(any(), eq(location));
        verify(effect).deploy(placedBlock);
        verify(faceAttachable).setAttachedFace(AttachedFace.FLOOR);
        verify(holder).setHeldItem(null);
    }

    @Test
    public void performReturnsTrueWhenPlacingBlockAgainstWall() {
        double health = 50.0;
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.5);

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

        ItemEffect effect = mock(ItemEffect.class);
        when(effect.isPrimed()).thenReturn(false);

        DeploymentProperties deploymentProperties = new DeploymentProperties();
        deploymentProperties.setHealth(health);
        deploymentProperties.setResistances(resistances);

        when(equipment.getDeploymentProperties()).thenReturn(deploymentProperties);
        when(equipment.getEffect()).thenReturn(effect);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlaceFunction function = new PlaceFunction(properties, equipment, audioEmitter, taskRunner);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlacedBlock> placedBlockCaptor = ArgumentCaptor.forClass(PlacedBlock.class);
        verify(equipment).onDeployDeploymentObject(placedBlockCaptor.capture());

        ArgumentCaptor<ItemEffectContext> contextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(effect).prime(contextCaptor.capture());

        PlacedBlock placedBlock = placedBlockCaptor.getValue();
        ItemEffectContext context = contextCaptor.getValue();

        assertEquals(health, placedBlock.getHealth());
        assertEquals(location, placedBlock.getLocation());
        assertEquals(resistances, placedBlock.getResistances());

        verify(equipment).onDeployDeploymentObject(placedBlock);
        verify(adjacentBlockState).setBlockData(faceAttachable);
        verify(adjacentBlockState).setBlockData(directional);
        verify(audioEmitter).playSounds(any(), eq(location));
        verify(directional).setFacing(targetBlockFace);
        verify(effect).prime(context);
        verify(faceAttachable).setAttachedFace(AttachedFace.WALL);
        verify(holder).setHeldItem(null);
    }
}
