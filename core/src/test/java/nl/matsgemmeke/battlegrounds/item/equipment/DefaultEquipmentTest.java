package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultEquipmentTest {

    private GameContext context;

    @Before
    public void setUp() {
        context = mock(GameContext.class);
    }

    @Test
    public void shouldOnlyBeDroppableWhenItemStackIsSet() {
        ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);

        DefaultEquipment equipment = new DefaultEquipment(context);
        equipment.setItemStack(itemStack);
        boolean droppable = equipment.canDrop();

        assertTrue(droppable);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowErrorWhenDroppingItemWithoutSetItemStack() {
        Location location = new Location(null, 1, 1, 1);

        DefaultEquipment equipment = new DefaultEquipment(context);
        equipment.dropItem(location);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowErrorWhenDroppingItemAtLocationWithoutWorld() {
        ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
        Location location = new Location(null, 1, 1, 1);

        DefaultEquipment equipment = new DefaultEquipment(context);
        equipment.setItemStack(itemStack);
        equipment.dropItem(location);
    }

    @Test
    public void shouldProduceNewItemEntityWhenDroppingItem() {
        ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        when(world.dropItem(location, itemStack)).thenReturn(mock(Item.class));

        DefaultEquipment equipment = new DefaultEquipment(context);
        equipment.setItemStack(itemStack);
        Item droppedItem = equipment.dropItem(location);

        assertNotNull(droppedItem);

        verify(world).dropItem(location, itemStack);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldPerformFunctionWhenLeftClicked() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        ItemFunction<EquipmentHolder> function = (ItemFunction<EquipmentHolder>) mock(ItemFunction.class);
        when(function.isAvailable()).thenReturn(true);

        DefaultEquipment equipment = new DefaultEquipment(context);
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

        DefaultEquipment equipment = new DefaultEquipment(context);
        equipment.getControls().addControl(Action.RIGHT_CLICK, function);
        equipment.setHolder(holder);
        equipment.onRightClick();

        verify(function).perform(holder);
    }
}
