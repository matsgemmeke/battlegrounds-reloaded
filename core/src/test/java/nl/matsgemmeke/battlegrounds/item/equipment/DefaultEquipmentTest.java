package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultEquipmentTest {

    private EntityRegistry<GameItem, Item> itemRegistry;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        itemRegistry = (EntityRegistry<GameItem, Item>) mock(EntityRegistry.class);
    }

    @Test
    public void shouldOnlyBeDroppableWhenItemStackIsSet() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        DefaultEquipment equipment = new DefaultEquipment(itemRegistry);
        equipment.setItemStack(itemStack);
        boolean droppable = equipment.canDrop();

        assertTrue(droppable);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowErrorWhenDroppingItemWithoutSetItemStack() {
        Location location = new Location(null, 1, 1, 1);

        DefaultEquipment equipment = new DefaultEquipment(itemRegistry);
        equipment.dropItem(location);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowErrorWhenDroppingItemAtLocationWithoutHolder() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);
        Location location = new Location(null, 1, 1, 1);

        DefaultEquipment equipment = new DefaultEquipment(itemRegistry);
        equipment.setItemStack(itemStack);
        equipment.dropItem(location);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowErrorWhenDroppingItemAtLocationWithoutWorld() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);
        Location location = new Location(null, 1, 1, 1);

        EquipmentHolder holder = mock(EquipmentHolder.class);

        DefaultEquipment equipment = new DefaultEquipment(itemRegistry);
        equipment.setItemStack(itemStack);
        equipment.setHolder(holder);
        equipment.dropItem(location);
    }

    @Test
    public void shouldProduceNewItemEntityWhenDroppingItem() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLocation()).thenReturn(location);

        when(world.dropItem(location, itemStack)).thenReturn(mock(Item.class));

        DefaultEquipment equipment = new DefaultEquipment(itemRegistry);
        equipment.setItemStack(itemStack);
        equipment.setHolder(holder);
        Item droppedItem = equipment.dropItem(location);

        assertNotNull(droppedItem);

        verify(itemRegistry).registerEntity(droppedItem);
        verify(world).dropItem(location, itemStack);
    }

    @Test
    public void shouldDoNothingIfHolderIsNullWhenDeploying() {
        DefaultEquipment equipment = new DefaultEquipment(itemRegistry);
        equipment.onDeploy();

        boolean deployed = equipment.isDeployed();

        assertFalse(deployed);
    }

    @Test
    public void shouldSetHolderHeldItemToActivatorItemStackWhenDeploying() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemStack activatorItemStack = new ItemStack(Material.SHEARS);

        DefaultEquipment equipment = new DefaultEquipment(itemRegistry);
        equipment.setActivatorItemStack(activatorItemStack);
        equipment.setHolder(holder);
        equipment.onDeploy();

        boolean deployed = equipment.isDeployed();

        assertTrue(deployed);

        verify(holder).setHeldItem(activatorItemStack);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldPerformFunctionWhenLeftClicked() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        ItemFunction<EquipmentHolder> function = (ItemFunction<EquipmentHolder>) mock(ItemFunction.class);
        when(function.isAvailable()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment(itemRegistry);
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

        DefaultEquipment equipment = new DefaultEquipment(itemRegistry);
        equipment.getControls().addControl(Action.RIGHT_CLICK, function);
        equipment.setHolder(holder);
        equipment.onRightClick();

        verify(function).perform(holder);
    }
}
