package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultEquipmentTest {

    @Test
    public void activateDeploymentActivatesDeploymentHandler() {
        Player player = mock(Player.class);
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(player);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        equipment.activateDeployment(holder);

        verify(deploymentHandler).activateDeployment(holder, player);
    }

    @Test
    public void getDeploymentObjectReturnsDeploymentObjectFromDeploymentHandler() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandler.getDeploymentObject()).thenReturn(deploymentObject);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        DeploymentObject result = equipment.getDeploymentObject();

        assertThat(result).isEqualTo(deploymentObject);
    }

    @Test
    public void isActivatorReadyReturnsFalseWhenActivatorIsNull() {
        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(null);
        boolean activatorReady = equipment.isActivatorReady();

        assertThat(activatorReady).isFalse();
    }

    @Test
    public void isActivatorReadyReturnsFalseWhenActivatorIsNotReadied() {
        Activator activator = mock(Activator.class);
        when(activator.isReady()).thenReturn(false);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);
        boolean activatorReady = equipment.isActivatorReady();

        assertThat(activatorReady).isFalse();
    }

    @Test
    public void isActivatorReadyReturnsFalseWhenActivatorIsReadied() {
        Activator activator = mock(Activator.class);
        when(activator.isReady()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);
        boolean activatorReady = equipment.isActivatorReady();

        assertThat(activatorReady).isTrue();
    }

    @Test
    public void isAwaitingDeploymentReturnsTrueWhenDeploymentHandlerIsAwaitingDeployment() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandler.isAwaitingDeployment()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        boolean awaitingDeployment = equipment.isAwaitingDeployment();

        assertThat(awaitingDeployment).isTrue();
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseWhenDeploymentHandlerIsNotAwaitingDeployment() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandler.isAwaitingDeployment()).thenReturn(false);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        boolean awaitingDeployment = equipment.isAwaitingDeployment();

        assertThat(awaitingDeployment).isFalse();
    }

    @Test
    public void isDeployedReturnsFalseWhenDeploymentHandlerHasNotHandledAnyDeployments() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandler.isDeployed()).thenReturn(false);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        boolean deployed = equipment.isDeployed();

        assertThat(deployed).isFalse();
    }

    @Test
    public void isDeployedReturnsTrueWhenDeploymentHandlerHasHandledDeployments() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandler.isDeployed()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        boolean deployed = equipment.isDeployed();

        assertThat(deployed).isTrue();
    }

    @Test
    public void matchesWithItemStackIfItemTemplateIsNotNullAndMatchesWithItsTemplate() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.matchesTemplate(itemStack)).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setItemTemplate(itemTemplate);

        boolean matches = equipment.isMatching(itemStack);

        assertTrue(matches);
    }

    @Test
    public void matchesWithItemStackIfActivatorIsNotNullAndMatchesWithTheItemStack() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Activator activator = mock(Activator.class);
        when(activator.isMatching(itemStack)).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);

        boolean matches = equipment.isMatching(itemStack);

        assertTrue(matches);
    }

    @Test
    public void doesNotMatchWithItemStackIfItDoesNotMatchWithEitherTheItemTemplateOrTheActivator() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Activator activator = mock(Activator.class);
        when(activator.isMatching(itemStack)).thenReturn(false);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.matchesTemplate(itemStack)).thenReturn(false);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);
        equipment.setItemTemplate(itemTemplate);

        boolean matches = equipment.isMatching(itemStack);

        assertFalse(matches);
    }

    @Test
    public void destroyDeploymentDelegatesToDeploymentHandler() {
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        equipment.destroyDeployment();

        verify(deploymentHandler).destroyDeployment();
    }

    @Test
    public void shouldPerformFunctionWhenLeftClicked() {
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
    public void shouldPerformFunctionWhenRightClicked() {
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
    public void performDeploymentCallsDeploymentHandler() {
        Deployment deployment = mock(Deployment.class);
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);

        Player player = mock(Player.class);
        when(holder.getEntity()).thenReturn(player);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentHandler(deploymentHandler);
        equipment.performDeployment(deployment, holder);

        verify(deploymentHandler).handleDeployment(deployment, holder, player);
    }

    @Test
    public void doesNotUpdateIfItemTemplateIsNull() {
        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setItemTemplate(null);
        boolean updated = equipment.update();

        assertFalse(updated);
    }

    @Test
    public void createNewItemStackFromTemplateWhenUpdatingAndSetHeldItemOfHolder() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack(any())).thenReturn(itemStack);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setHolder(holder);
        equipment.setItemTemplate(itemTemplate);
        equipment.setName("test");
        equipment.setItemStack(itemStack);
        boolean updated = equipment.update();

        assertTrue(updated);

        verify(holder).setHeldItem(itemStack);
    }
}
