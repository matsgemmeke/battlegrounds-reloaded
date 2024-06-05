package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemRegisterTest {

    @Test
    public void shouldFindItemByItsCorrespondingItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        ItemRegister<Gun, GunHolder> register = new ItemRegister<>();
        register.addUnassignedItem(gun);

        Gun result = register.getUnassignedItem(itemStack);

        assertEquals(gun, result);
    }

    @Test
    public void shouldReturnNullWhenFindingNonMatchingItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);

        ItemRegister<Gun, GunHolder> register = new ItemRegister<>();
        register.addUnassignedItem(gun);

        Gun result = register.getUnassignedItem(itemStack);

        assertNull(result);
    }

    @Test
    public void shouldFindItemByItsCorrespondingItemStackAndHolder() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        GunHolder holder = mock(GunHolder.class);

        ItemRegister<Gun, GunHolder> register = new ItemRegister<>();
        register.addAssignedItem(gun, holder);

        Gun result = register.getAssignedItem(holder, itemStack);

        assertEquals(gun, result);
    }
    
    @Test
    public void shouldReturnNullWhenHolderIsNotInRegister() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        
        GunHolder holder = mock(GunHolder.class);

        ItemRegister<Gun, GunHolder> register = new ItemRegister<>();
        Gun result = register.getAssignedItem(holder, itemStack);
        
        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenHolderDoesNotHaveItemStack() {
        ItemStack notAdded = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        GunHolder holder = mock(GunHolder.class);

        ItemRegister<Gun, GunHolder> register = new ItemRegister<>();
        register.addAssignedItem(gun, holder);

        Gun result = register.getAssignedItem(holder, notAdded);

        assertNull(result);
    }

    @Test
    public void shouldRemoveItemFromRegister() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        ItemRegister<Gun, GunHolder> register = new ItemRegister<>();
        register.addUnassignedItem(gun);
        register.removeUnassignedItem(gun);

        Gun result = register.getUnassignedItem(itemStack);

        assertNull(result);
    }

    @Test
    public void shouldRemoveItemFromHolderIfBothRegistered() {
        Gun gun = mock(Gun.class);

        GunHolder holder = mock(GunHolder.class);

        ItemRegister<Gun, GunHolder> register = new ItemRegister<>();
        register.addAssignedItem(gun, holder);

        boolean result = register.removeAssignedItem(gun, holder);

        assertTrue(result);
    }

    @Test
    public void shouldNotRemoveItemFromHolderIfRegisterDoesNotContainHolder() {
        Gun gun = mock(Gun.class);

        GunHolder holder = mock(GunHolder.class);

        ItemRegister<Gun, GunHolder> register = new ItemRegister<>();
        boolean result = register.removeAssignedItem(gun, holder);

        assertFalse(result);
    }

    @Test
    public void shouldNotRemoveItemFromHolderIfTheyDoNotHaveAny() {
        Gun gun = mock(Gun.class);

        GunHolder holder = mock(GunHolder.class);

        ItemRegister<Gun, GunHolder> register = new ItemRegister<>();
        register.addAssignedItem(mock(Gun.class), holder);

        boolean result = register.removeAssignedItem(gun, holder);

        assertFalse(result);
    }
}
