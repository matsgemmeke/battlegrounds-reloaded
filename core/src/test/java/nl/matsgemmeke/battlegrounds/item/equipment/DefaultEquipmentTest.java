package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultEquipmentTest {

    @Test
    public void matchesWithItemStackIfSimilar() {
        ItemStack other = new ItemStack(Material.SHEARS);

        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.isSimilar(other)).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setItemStack(itemStack);

        boolean matching = equipment.isMatching(other);

        assertTrue(matching);
    }

    @Test
    public void matchesWithItemStackIfSimilarToActivatorItemStack() {
        ItemStack other = new ItemStack(Material.SHEARS);

        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.isSimilar(other)).thenReturn(true);

        Activator activator = mock(Activator.class);
        when(activator.getItemStack()).thenReturn(itemStack);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);
        equipment.setItemStack(null);

        boolean matching = equipment.isMatching(other);

        assertTrue(matching);
    }

    @Test
    public void doesNotMatchIfEquipmentItemStackIsNullAndActivatorIsNull() {
        ItemStack other = new ItemStack(Material.SHEARS);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(null);
        equipment.setItemStack(null);

        boolean matching = equipment.isMatching(other);

        assertFalse(matching);
    }

    @Test
    public void doesNotMatchIfEquipmentItemStackIsNullAndActivatorItemStackIsNull() {
        ItemStack other = new ItemStack(Material.SHEARS);

        Activator activator = mock(Activator.class);
        when(activator.getItemStack()).thenReturn(null);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);
        equipment.setItemStack(null);

        boolean matching = equipment.isMatching(other);

        assertFalse(matching);
    }

    @Test
    public void doesNotMatchIfEquipmentItemStackIsNullAndActivatorItemStackIsNotSimilar() {
        ItemStack other = new ItemStack(Material.SHEARS);

        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.isSimilar(other)).thenReturn(false);

        Activator activator = mock(Activator.class);
        when(activator.getItemStack()).thenReturn(itemStack);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);
        equipment.setItemStack(null);

        boolean matching = equipment.isMatching(other);

        assertFalse(matching);
    }

    @Test
    public void shouldDoNothingIfHolderIsNullWhenDeploying() {
        Deployable object = mock(Deployable.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.onDeploy(object);

        assertTrue(equipment.getDeployedObjects().isEmpty());
    }

    @Test
    public void prepareActivatorIfNotNullWhenDeployingObject() {
        Deployable object = mock(Deployable.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack activatorItemStack = new ItemStack(Material.SHEARS);

        Activator activator = mock(Activator.class);
        when(activator.getItemStack()).thenReturn(activatorItemStack);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(activator);
        equipment.setHolder(holder);
        equipment.onDeploy(object);

        assertEquals(1, equipment.getDeployedObjects().size());
        assertEquals(object, equipment.getDeployedObjects().get(0));

        verify(activator).prepare(any());
        verify(holder).setHeldItem(activatorItemStack);
    }

    @Test
    public void removeItemStackFromHolderIfActivatorIsNullWhenDeploying() {
        Deployable object = mock(Deployable.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivator(null);
        equipment.setHolder(holder);
        equipment.onDeploy(object);

        assertEquals(1, equipment.getDeployedObjects().size());
        assertEquals(object, equipment.getDeployedObjects().get(0));

        verify(holder).setHeldItem(null);
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
