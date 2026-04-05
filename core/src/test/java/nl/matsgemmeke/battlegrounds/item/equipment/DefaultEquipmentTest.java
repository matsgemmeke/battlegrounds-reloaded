package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentAction;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.Activator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @Disabled
    @Test
    void activateDeploymentActivatesDeploymentHandler() {
//        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        EquipmentUser user = mock(EquipmentUser.class);

        DefaultEquipment equipment = new DefaultEquipment();
//        equipment.setDeploymentHandler(deploymentHandler);
        equipment.activateDeployment(user);

//        verify(deploymentHandler).activateDeployment(user);
    }

    @Disabled
    @Test
    void cleanupDelegatesToDeploymentHandler() {
//        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);

        DefaultEquipment equipment = new DefaultEquipment();
//        equipment.setDeploymentHandler(deploymentHandler);
        equipment.cleanup();

//        verify(deploymentHandler).cleanupDeployment();
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

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isAwaitingDeployment returns whether deployment is pending")
    void isAwaitingDeployment(boolean pending, boolean expectedAwaitingDeployment) {
        Deployment deployment = mock(Deployment.class);
        when(deployment.isPending()).thenReturn(pending);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeployment(deployment);
        boolean awaitingDeployment = equipment.isAwaitingDeployment();

        assertThat(awaitingDeployment).isEqualTo(expectedAwaitingDeployment);
    }

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isDeployed returns whether deployment is performing")
    void isDeployedReturnsWhetherDeploymentHandlerHasDeployedState(boolean performing, boolean expectedDeployed) {
        Deployment deployment = mock(Deployment.class);
        when(deployment.isPerforming()).thenReturn(performing);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeployment(deployment);
        boolean result = equipment.isDeployed();

        assertThat(result).isEqualTo(expectedDeployed);
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
    void shouldPerformFunctionWhenLeftClicked() {
        EquipmentUser user = mock(EquipmentUser.class);

        ItemFunction<EquipmentUser> function = mock();
        when(function.isAvailable()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.getControls().addControl(Action.LEFT_CLICK, function);
        equipment.setUser(user);
        equipment.onLeftClick();

        verify(function).perform(user);
    }

    @Test
    void shouldPerformFunctionWhenRightClicked() {
        EquipmentUser user = mock(EquipmentUser.class);

        ItemFunction<EquipmentUser> function = mock();
        when(function.isAvailable()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.getControls().addControl(Action.RIGHT_CLICK, function);
        equipment.setUser(user);
        equipment.onRightClick();

        verify(function).perform(user);
    }

    @Test
    @DisplayName("performDeployment does nothing when deployment action does not produce a deployment result")
    void performDeployment_deploymentActionWithoutResult() {
        Deployment deployment = mock(Deployment.class);
        EquipmentUser user = mock(EquipmentUser.class);

        Player player = mock(Player.class);
        when(user.getEntity()).thenReturn(player);

        DeploymentAction deploymentAction = mock(DeploymentAction.class);
        when(deploymentAction.perform(eq(user), eq(player), any(DestructionListener.class))).thenReturn(Optional.empty());

        equipment.setDeployment(deployment);
        equipment.performDeploymentAction(deploymentAction, user);

        verifyNoInteractions(deployment);
    }

    @Test
    @DisplayName("performDeployment calls deployment when deployment action produces result")
    void performDeployment_deploymentActionWithResult() {
        Deployment deployment = mock(Deployment.class);
        DeploymentResult deploymentResult = new DeploymentResult(null, null, null, 0L);
        EquipmentUser user = mock(EquipmentUser.class);

        Player player = mock(Player.class);
        when(user.getEntity()).thenReturn(player);

        DeploymentAction deploymentAction = mock(DeploymentAction.class);
        when(deploymentAction.perform(eq(user), eq(player), any(DestructionListener.class))).thenReturn(Optional.of(deploymentResult));

        equipment.setDeployment(deployment);
        equipment.performDeploymentAction(deploymentAction, user);

        verify(deployment).processDeploymentResult(deploymentResult);
    }

    @Test
    void doesNotUpdateIfDisplayItemTemplateIsNull() {
        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDisplayItemTemplate(null);
        boolean updated = equipment.update();

        assertFalse(updated);
    }

    @Test
    @DisplayName("update creates new ItemStack from display item template and sets held item of user")
    void update_success() {
        EquipmentUser user = mock(EquipmentUser.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.createItemStack(any())).thenReturn(itemStack);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setUser(user);
        equipment.setDisplayItemTemplate(displayItemTemplate);
        equipment.setName("test");
        equipment.setItemStack(itemStack);
        boolean updated = equipment.update();

        assertThat(updated).isTrue();
    }
}
