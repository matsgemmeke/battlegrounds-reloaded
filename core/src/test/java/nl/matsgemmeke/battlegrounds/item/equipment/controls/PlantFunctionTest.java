package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.deployment.PlantableObject;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.FaceAttachable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlantFunctionTest {

    private AudioEmitter audioEmitter;
    private Deployable item;
    private ItemMechanismActivation mechanismActivation;
    private Material material;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        item = mock(Deployable.class);
        mechanismActivation = mock(ItemMechanismActivation.class);
        material = Material.WARPED_BUTTON;
    }

    @Test
    public void shouldBeAvailableIfDeployableItemIsNotYetDeployed() {
        when(item.isDeployed()).thenReturn(false);

        PlantFunction function = new PlantFunction(item, mechanismActivation, material, audioEmitter);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void shouldNotBeAvailableIfDeployableItemIsDeployed() {
        when(item.isDeployed()).thenReturn(true);

        PlantFunction function = new PlantFunction(item, mechanismActivation, material, audioEmitter);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldNotPerformIfHolderDoesNotReturnTwoTargetBlocks() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(Collections.emptyList());

        PlantFunction function = new PlantFunction(item, mechanismActivation, material, audioEmitter);
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

        PlantFunction function = new PlantFunction(item, mechanismActivation, material, audioEmitter);
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

        PlantFunction function = new PlantFunction(item, mechanismActivation, material, audioEmitter);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(item);
        verifyNoInteractions(mechanismActivation);
    }

    @Test
    public void shouldApplyFunctionsForPlantingWhenPerforming() {
        BlockFace targetBlockFace = BlockFace.UP;
        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        Location location = new Location(null, 1, 1, 1);

        Block adjacentBlock = mock(Block.class);
        when(adjacentBlock.getBlockData()).thenReturn(faceAttachable);
        when(adjacentBlock.getLocation()).thenReturn(location);
        when(adjacentBlock.getState()).thenReturn(mock(BlockState.class));

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        List<GameSound> sounds = List.of(mock(GameSound.class));

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlantFunction function = new PlantFunction(item, mechanismActivation, material, audioEmitter);
        function.addSounds(sounds);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        ArgumentCaptor<PlantableObject> captor = ArgumentCaptor.forClass(PlantableObject.class);
        verify(item).onDeploy(captor.capture());

        assertEquals(location, captor.getValue().getLocation());

        verify(audioEmitter).playSounds(any(), eq(location));
        verify(mechanismActivation).prime(holder);
    }
}
