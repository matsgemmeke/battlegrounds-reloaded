package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.deployment.PlantDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PlantFunctionTest {

    private Deployable item;
    private ItemMechanismActivation mechanismActivation;
    private PlantDeployment deployment;

    @Before
    public void setUp() {
        item = mock(Deployable.class);
        mechanismActivation = mock(ItemMechanismActivation.class);
        deployment = mock(PlantDeployment.class);
    }

    @Test
    public void shouldBeAvailableIfDeployableItemIsNotYetDeployed() {
        when(item.isDeployed()).thenReturn(false);

        PlantFunction function = new PlantFunction(item, mechanismActivation, deployment);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void shouldNotBeAvailableIfDeployableItemIsDeployed() {
        when(item.isDeployed()).thenReturn(true);

        PlantFunction function = new PlantFunction(item, mechanismActivation, deployment);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldNotPerformIfHolderDoesNotReturnTwoTargetBlocks() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(Collections.emptyList());

        PlantFunction function = new PlantFunction(item, mechanismActivation, deployment);
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

        PlantFunction function = new PlantFunction(item, mechanismActivation, deployment);
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

        PlantFunction function = new PlantFunction(item, mechanismActivation, deployment);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verifyNoInteractions(item);
        verifyNoInteractions(mechanismActivation);
    }

    @Test
    public void shouldApplyFunctionsForPlantingWhenPerforming() {
        Block adjacentBlock = mock(Block.class);
        BlockFace targetBlockFace = BlockFace.NORTH;

        Block targetBlock = mock(Block.class);
        when(targetBlock.getFace(adjacentBlock)).thenReturn(targetBlockFace);
        when(targetBlock.getType()).thenReturn(Material.OAK_LOG);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLastTwoTargetBlocks(4)).thenReturn(List.of(adjacentBlock, targetBlock));

        PlantFunction function = new PlantFunction(item, mechanismActivation, deployment);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(item).onDeploy();
        verify(mechanismActivation).prime(holder);
        verify(deployment).plant(adjacentBlock, targetBlockFace);
    }
}
