package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultEquipmentTest {

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
    public void onDeployDeploymentObjectAddsDeploymentObjectToList() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.onDeployDeploymentObject(deploymentObject);

        assertEquals(1, equipment.getDeploymentObjects().size());
        assertEquals(deploymentObject, equipment.getDeploymentObjects().get(0));
    }

    @Test
    public void onDestroyDeploymentObjectRemovesDeploymentObjectFromList() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.onDeployDeploymentObject(deploymentObject);
        equipment.onDestroyDeploymentObject(deploymentObject);

        assertTrue(equipment.getDeploymentObjects().isEmpty());
    }

    @Test
    public void onDestroyDeploymentObjectDoesNotActivateEffectIfDeploymentPropertiesIsNull() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        ItemEffect effect = mock(ItemEffect.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentProperties(null);
        equipment.setEffect(effect);
        equipment.onDestroyDeploymentObject(deploymentObject);

        verify(effect, never()).activateInstantly();
    }

    @Test
    public void onDestroyDeploymentObjectDoesNotActivateEffectIfActivatedOnDestroyIsFalse() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        ItemEffect effect = mock(ItemEffect.class);

        DeploymentProperties deploymentProperties = new DeploymentProperties();
        deploymentProperties.setActivatedOnDestroy(false);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentProperties(deploymentProperties);
        equipment.setEffect(effect);
        equipment.onDestroyDeploymentObject(deploymentObject);

        verify(effect, never()).activateInstantly();
    }

    @Test
    public void onDestroyDeploymentObjectDoesNotActivateEffectIfEffectIsNull() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        DeploymentProperties deploymentProperties = new DeploymentProperties();
        deploymentProperties.setActivatedOnDestroy(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentProperties(deploymentProperties);
        equipment.setEffect(null);

        assertDoesNotThrow(() -> equipment.onDestroyDeploymentObject(deploymentObject));
    }

    @Test
    public void onDestroyDeploymentObjectActivatesEffectIfActivatedOnDestroyIsTrueAndEffectIsNotNull() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        ItemEffect effect = mock(ItemEffect.class);

        DeploymentProperties deploymentProperties = new DeploymentProperties();
        deploymentProperties.setActivatedOnDestroy(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentProperties(deploymentProperties);
        equipment.setEffect(effect);
        equipment.onDestroyDeploymentObject(deploymentObject);

        verify(effect).activateInstantly();
    }

    @Test
    public void onDestroyDeploymentObjectDoesNotResetEffectIfDeploymentPropertiesIsNull() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        ItemEffect effect = mock(ItemEffect.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentProperties(null);
        equipment.setEffect(effect);
        equipment.onDestroyDeploymentObject(deploymentObject);

        verify(effect, never()).reset();
    }

    @Test
    public void onDestroyDeploymentObjectDoesNotResetEffectIfResetOnDestroyIsFalse() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        ItemEffect effect = mock(ItemEffect.class);

        DeploymentProperties deploymentProperties = new DeploymentProperties();
        deploymentProperties.setResetOnDestroy(false);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentProperties(deploymentProperties);
        equipment.setEffect(effect);
        equipment.onDestroyDeploymentObject(deploymentObject);

        verify(effect, never()).activateInstantly();
    }

    @Test
    public void onDestroyDeploymentObjectDoesNotResetEffectIfEffectIsNull() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        DeploymentProperties deploymentProperties = new DeploymentProperties();
        deploymentProperties.setResetOnDestroy(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentProperties(deploymentProperties);
        equipment.setEffect(null);

        assertDoesNotThrow(() -> equipment.onDestroyDeploymentObject(deploymentObject));
    }

    @Test
    public void onDestroyDeploymentObjectResetsEffectIfResetOnDestroyIsTrueAndEffectIsNotNull() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        ItemEffect effect = mock(ItemEffect.class);

        DeploymentProperties deploymentProperties = new DeploymentProperties();
        deploymentProperties.setResetOnDestroy(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDeploymentProperties(deploymentProperties);
        equipment.setEffect(effect);
        equipment.onDestroyDeploymentObject(deploymentObject);

        verify(effect).reset();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldPerformFunctionWhenLeftClicked() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        ItemFunction<EquipmentHolder> function = (ItemFunction<EquipmentHolder>) mock(ItemFunction.class);
        when(function.isAvailable()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.getControls().addControl(Action.LEFT_CLICK, function);
        equipment.setHolder(holder);
        equipment.onLeftClick();

        verify(function).perform(holder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldPerformFunctionWhenRightClicked() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        ItemFunction<EquipmentHolder> function = (ItemFunction<EquipmentHolder>) mock(ItemFunction.class);
        when(function.isAvailable()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.getControls().addControl(Action.RIGHT_CLICK, function);
        equipment.setHolder(holder);
        equipment.onRightClick();

        verify(function).perform(holder);
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
