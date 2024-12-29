package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultActivatorTest {

    private ItemTemplate itemTemplate;

    @BeforeEach
    public void setUp() {
        itemTemplate = mock(ItemTemplate.class);
    }

    @Test
    public void isReadyReturnsFalseWhenActivatorIsNotPrepared() {
        DefaultActivator activator = new DefaultActivator(itemTemplate);
        boolean ready = activator.isReady();

        assertFalse(ready);
    }

    @Test
    public void isReadyReturnsTrueWhenActivatorIsPrepared() {
        ItemHolder holder = mock(ItemHolder.class);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        activator.prepare(holder);
        boolean ready = activator.isReady();

        assertTrue(ready);
    }

    @Test
    public void matchesWithItemStackIfItMatchesWithTemplate() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        when(itemTemplate.matchesTemplate(itemStack)).thenReturn(true);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        boolean matches = activator.isMatching(itemStack);

        assertTrue(matches);
    }

    @Test
    public void doesNotMatchWithItemStackIfItDoesNotMatchWithTemplate() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        when(itemTemplate.matchesTemplate(itemStack)).thenReturn(false);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        boolean matches = activator.isMatching(itemStack);

        assertFalse(matches);
    }

    @Test
    public void prepareReadiesTheActivatorOnceAndSetHolderHeldItem() {
        ItemHolder holder = mock(ItemHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        activator.prepare(holder);
        activator.prepare(holder);

        assertTrue(activator.isReady());

        verify(holder).setHeldItem(itemStack);
    }

    @Test
    public void removeReturnsFalseIfActivatorIsNotPrepared() {
        DefaultActivator activator = new DefaultActivator(itemTemplate);
        boolean removed = activator.remove();

        assertFalse(removed);
    }

    @Test
    public void removeReturnsTrueRemovesItemFromHolderIfActivatorIsPrepared() {
        ItemHolder holder = mock(ItemHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        activator.prepare(holder);
        boolean removed = activator.remove();

        assertTrue(removed);

        verify(holder).removeItem(itemStack);
    }
}
