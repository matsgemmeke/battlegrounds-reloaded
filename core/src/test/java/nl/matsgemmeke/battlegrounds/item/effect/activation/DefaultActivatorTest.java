package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultActivatorTest {

    @Test
    public void matchesWithItemStackIfItMatchesWithTemplate() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.matchesTemplate(itemStack)).thenReturn(true);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        boolean matches = activator.isMatching(itemStack);

        assertTrue(matches);
    }

    @Test
    public void doesNotMatchWithItemStackIfItDoesNotMatchWithTemplate() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.matchesTemplate(itemStack)).thenReturn(false);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        boolean matches = activator.isMatching(itemStack);

        assertFalse(matches);
    }

    @Test
    public void readiesTheActivatorAndSetHolderHeldItemWhenPreparing() {
        ItemHolder holder = mock(ItemHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        activator.prepare(holder);

        assertTrue(activator.isReady());

        verify(holder).setHeldItem(itemStack);
    }
}
