package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultEquipmentTest {

    @Test
    public void shouldDoNothingIfHolderIsNullWhenDeploying() {
        Deployable object = mock(Deployable.class);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.onDeploy(object);

        assertTrue(equipment.getDeployedObjects().isEmpty());
    }

    @Test
    public void shouldSetHolderHeldItemToActivatorItemStackWhenDeploying() {
        Deployable object = mock(Deployable.class);
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack activatorItemStack = new ItemStack(Material.SHEARS);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setActivatorItemStack(activatorItemStack);
        equipment.setHolder(holder);
        equipment.onDeploy(object);

        assertEquals(1, equipment.getDeployedObjects().size());

        verify(holder).setHeldItem(activatorItemStack);
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
    public void doesNotUpdateIfItemStackIsNull() {
        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setItemStack(null);
        boolean updated = equipment.update();

        assertFalse(updated);
    }

    @Test
    public void doesNotUpdateIfItemMetaIsNull() {
        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.getItemMeta()).thenReturn(null);

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setItemStack(itemStack);
        boolean updated = equipment.update();

        assertFalse(updated);
    }

    @Test
    public void updateItemStackDisplayName() {
        ItemMeta itemMeta = mock(ItemMeta.class);

        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.getItemMeta()).thenReturn(itemMeta);

        TextTemplate displayNameTemplate = new TextTemplate("%name%");

        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setDisplayNameTemplate(displayNameTemplate);
        equipment.setName("test");
        equipment.setItemStack(itemStack);
        boolean updated = equipment.update();

        assertTrue(updated);
    }
}
