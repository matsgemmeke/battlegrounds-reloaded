package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import nl.matsgemmeke.battlegrounds.item.effect.Activator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultEquipmentTest {

    private DefaultEquipment equipment;

    @BeforeEach
    void setUp() {
        equipment = new DefaultEquipment();
    }

    @Test
    void activateDeploymentActivatesDeploymentHandler() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        equipment.activateDeployment(holder);

        verify(deploymentHandler).activateDeployment(holder);
    }

    @Test
    void cleanupDelegatesToDeploymentHandler() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        equipment.cleanup();

        verify(deploymentHandler).cleanupDeployment();
    }

    @Test
    void getDeploymentObjectReturnsDeploymentObjectFromDeploymentHandler() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandler.getDeploymentObject()).thenReturn(deploymentObject);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        DeploymentObject result = equipment.getDeploymentObject();

        assertThat(result).isEqualTo(deploymentObject);
    }

    @Test
    void isActivatorReadyReturnsFalseWhenActivatorIsNull() {
        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(null);
        boolean activatorReady = equipment.isActivatorReady();

        assertThat(activatorReady).isFalse();
    }

    @Test
    void isActivatorReadyReturnsFalseWhenActivatorIsNotReadied() {
        Activator activator = mock(Activator.class);
        when(activator.isReady()).thenReturn(false);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);
        boolean activatorReady = equipment.isActivatorReady();

        assertThat(activatorReady).isFalse();
    }

    @Test
    void isActivatorReadyReturnsFalseWhenActivatorIsReadied() {
        Activator activator = mock(Activator.class);
        when(activator.isReady()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);
        boolean activatorReady = equipment.isActivatorReady();

        assertThat(activatorReady).isTrue();
    }

    @Test
    void isAwaitingDeploymentReturnsTrueWhenDeploymentHandlerIsAwaitingDeployment() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandler.isAwaitingDeployment()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        boolean awaitingDeployment = equipment.isAwaitingDeployment();

        assertThat(awaitingDeployment).isTrue();
    }

    @Test
    void isAwaitingDeploymentReturnsFalseWhenDeploymentHandlerIsNotAwaitingDeployment() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandler.isAwaitingDeployment()).thenReturn(false);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        boolean awaitingDeployment = equipment.isAwaitingDeployment();

        assertThat(awaitingDeployment).isFalse();
    }

    @Test
    void isDeployedReturnsFalseWhenDeploymentHandlerIsNotPerforming() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandler.isPerforming()).thenReturn(false);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        boolean deployed = equipment.isDeployed();

        assertThat(deployed).isFalse();
    }

    @Test
    void isDeployedReturnsTrueWhenDeploymentHandlerIsPerforming() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandler.isPerforming()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        boolean deployed = equipment.isDeployed();

        assertThat(deployed).isTrue();
    }

    @Test
    void matchesWithItemStackIfItemTemplateIsNotNullAndMatchesWithDisplayItemTemplate() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.matchesTemplate(itemStack)).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDisplayItemTemplate(displayItemTemplate);

        boolean matches = equipment.isMatching(itemStack);

        assertTrue(matches);
    }

    @Test
    void matchesWithItemStackIfActivatorIsNotNullAndMatchesWithTheItemStack() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Activator activator = mock(Activator.class);
        when(activator.isMatching(itemStack)).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);

        boolean matches = equipment.isMatching(itemStack);

        assertTrue(matches);
    }

    @Test
    void doesNotMatchWithItemStackIfItDoesNotMatchWithEitherDisplayItemOrActivator() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Activator activator = mock(Activator.class);
        when(activator.isMatching(itemStack)).thenReturn(false);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.matchesTemplate(itemStack)).thenReturn(false);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);
        equipment.setDisplayItemTemplate(displayItemTemplate);

        boolean matches = equipment.isMatching(itemStack);

        assertFalse(matches);
    }

    @Test
    void destroyDeploymentDelegatesToDeploymentHandler() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        equipment.destroyDeployment();

        verify(deploymentHandler).destroyDeployment();
    }

    @Test
    void shouldPerformFunctionWhenLeftClicked() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        ItemFunction<EquipmentHolder> function = mock();
        when(function.isAvailable()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.getControls().addControl(Action.LEFT_CLICK, function);
        equipment.setHolder(holder);
        equipment.onLeftClick();

        verify(function).perform(holder);
    }

    @Test
    void shouldPerformFunctionWhenRightClicked() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        ItemFunction<EquipmentHolder> function = mock();
        when(function.isAvailable()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.getControls().addControl(Action.RIGHT_CLICK, function);
        equipment.setHolder(holder);
        equipment.onRightClick();

        verify(function).perform(holder);
    }

    @Test
    void performDeploymentDoesNothingWhenDeploymentProducesNoDeploymentContext() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);

        Player player = mock(Player.class);
        when(holder.getEntity()).thenReturn(player);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(eq(holder), eq(player), any(DestructionListener.class))).thenReturn(Optional.empty());

        equipment.setDeploymentHandler(deploymentHandler);
        equipment.performDeployment(deployment, holder);

        verifyNoInteractions(deploymentHandler);
    }

    @Test
    void performDeploymentCallsDeploymentHandlerWhenDeploymentProducesDeploymentContext() {
        DeploymentResult deploymentResult = new DeploymentResult(null, null, 0L);
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);

        Player player = mock(Player.class);
        when(holder.getEntity()).thenReturn(player);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(eq(holder), eq(player), any(DestructionListener.class))).thenReturn(Optional.of(deploymentResult));

        equipment.setDeploymentHandler(deploymentHandler);
        equipment.performDeployment(deployment, holder);

        verify(deploymentHandler).processDeploymentResult(deploymentResult);
    }

    @Test
    void doesNotUpdateIfDisplayItemTemplateIsNull() {
        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDisplayItemTemplate(null);
        boolean updated = equipment.update();

        assertFalse(updated);
    }

    @Test
    void createNewItemStackFromTemplateWhenUpdatingAndSetHeldItemOfHolder() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.createItemStack(any())).thenReturn(itemStack);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setHolder(holder);
        equipment.setDisplayItemTemplate(displayItemTemplate);
        equipment.setName("test");
        equipment.setItemStack(itemStack);
        boolean updated = equipment.update();

        assertTrue(updated);
    }
}
