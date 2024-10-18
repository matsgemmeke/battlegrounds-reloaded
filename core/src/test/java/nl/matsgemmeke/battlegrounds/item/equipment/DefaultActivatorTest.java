package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultActivatorTest {

    @Test
    public void readiesTheActivatorAndCreatesItemStackWhenPreparing() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);
        Map<String, Object> values = Collections.emptyMap();

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack(values)).thenReturn(itemStack);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        activator.prepare(values);

        assertEquals(itemStack, activator.getItemStack());
        assertTrue(activator.isReady());
    }
}
