package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private ItemStack itemStack;

    @Before
    public void setUp() {
        itemStack = new ItemStack(Material.FLINT_AND_STEEL);
    }

    @Test
    public void shouldThrowItemWhenPerforming() {
        Item item = mock(Item.class);

        World world = mock(World.class);
        when(world.dropItem(any(), any())).thenReturn(item);

        Location location = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(location);
        when(entity.getWorld()).thenReturn(world);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);
        when(holder.getThrowingDirection()).thenReturn(location);

        ThrowFunction function = new ThrowFunction(itemStack, 2.0);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(world).dropItem(location, itemStack);
    }
}
