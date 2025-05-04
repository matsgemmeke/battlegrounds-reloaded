package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
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
        Deployer deployer = mock(Deployer.class);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        activator.prepare(deployer);
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
    public void prepareReadiesTheActivatorOnceAndSetsDeployerHeldItem() {
        Deployer deployer = mock(Deployer.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        activator.prepare(deployer);
        activator.prepare(deployer);

        assertTrue(activator.isReady());

        verify(deployer).setHeldItem(itemStack);
    }

    @Test
    public void removeReturnsFalseIfActivatorIsNotPrepared() {
        DefaultActivator activator = new DefaultActivator(itemTemplate);
        boolean removed = activator.remove();

        assertFalse(removed);
    }

    @Test
    public void removeReturnsTrueRemovesItemFromDeployerIfActivatorIsPrepared() {
        Deployer deployer = mock(Deployer.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        DefaultActivator activator = new DefaultActivator(itemTemplate);
        activator.prepare(deployer);
        boolean removed = activator.remove();

        assertTrue(removed);

        verify(deployer).removeItem(itemStack);
    }
}
