package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemStorageTest {

    @Test
    public void shouldFindItemByItsCorrespondingItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        storage.addUnassignedItem(gun);

        Gun result = storage.getUnassignedItem(itemStack);

        assertEquals(gun, result);
    }

    @Test
    public void shouldReturnNullWhenFindingNonMatchingItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        storage.addUnassignedItem(gun);

        Gun result = storage.getUnassignedItem(itemStack);

        assertNull(result);
    }

    @Test
    public void shouldFindItemByItsCorrespondingItemStackAndHolder() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        GunHolder holder = mock(GunHolder.class);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        storage.addAssignedItem(gun, holder);

        Gun result = storage.getAssignedItem(holder, itemStack);

        assertEquals(gun, result);
    }
    
    @Test
    public void shouldReturnNullWhenHolderIsNotInStorage() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        
        GunHolder holder = mock(GunHolder.class);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        Gun result = storage.getAssignedItem(holder, itemStack);
        
        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenHolderDoesNotHaveItemStack() {
        ItemStack notAdded = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        GunHolder holder = mock(GunHolder.class);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        storage.addAssignedItem(gun, holder);

        Gun result = storage.getAssignedItem(holder, notAdded);

        assertNull(result);
    }

    @Test
    public void getAssignedItemsReturnsEmptyListIfInstanceDoesNotContainRecordWithGivenHolder() {
        GunHolder holder = mock(GunHolder.class);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        List<Gun> assignedGuns = storage.getAssignedItems(holder);

        assertEquals(0, assignedGuns.size());
    }

    @Test
    public void getAssignedItemsReturnsListOfAssignedItemsToTheHolder() {
        Gun gun = mock(Gun.class);
        GunHolder holder = mock(GunHolder.class);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        storage.addAssignedItem(gun, holder);

        List<Gun> assignedGuns = storage.getAssignedItems(holder);

        assertEquals(1, assignedGuns.size());
        assertEquals(gun, assignedGuns.get(0));
    }

    @Test
    public void shouldRemoveItemFromStorage() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        storage.addUnassignedItem(gun);
        storage.removeUnassignedItem(gun);

        Gun result = storage.getUnassignedItem(itemStack);

        assertNull(result);
    }

    @Test
    public void shouldRemoveItemFromHolderIfBothRegistered() {
        Gun gun = mock(Gun.class);

        GunHolder holder = mock(GunHolder.class);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        storage.addAssignedItem(gun, holder);

        boolean result = storage.removeAssignedItem(gun, holder);

        assertTrue(result);
    }

    @Test
    public void shouldNotRemoveItemFromHolderIfStorageDoesNotContainHolder() {
        Gun gun = mock(Gun.class);

        GunHolder holder = mock(GunHolder.class);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        boolean result = storage.removeAssignedItem(gun, holder);

        assertFalse(result);
    }

    @Test
    public void shouldNotRemoveItemFromHolderIfTheyDoNotHaveAny() {
        Gun gun = mock(Gun.class);

        GunHolder holder = mock(GunHolder.class);

        ItemStorage<Gun, GunHolder> storage = new ItemStorage<>();
        storage.addAssignedItem(mock(Gun.class), holder);

        boolean result = storage.removeAssignedItem(gun, holder);

        assertFalse(result);
    }
}
