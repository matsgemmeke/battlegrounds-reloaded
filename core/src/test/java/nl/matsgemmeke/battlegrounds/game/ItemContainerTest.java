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

public class ItemContainerTest {

    @Test
    public void getAllItemsReturnsListContainingAllEntriesFromAssignedAndUnassignedList() {
        Gun assignedGun = mock(Gun.class);
        Gun unassignedGun = mock(Gun.class);
        GunHolder holder = mock(GunHolder.class);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        container.addUnassignedItem(unassignedGun);
        container.addAssignedItem(assignedGun, holder);

        List<Gun> guns = container.getAllItems();

        assertEquals(2, guns.size());
        assertEquals(assignedGun, guns.get(0));
        assertEquals(unassignedGun, guns.get(1));
    }

    @Test
    public void shouldFindItemByItsCorrespondingItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        container.addUnassignedItem(gun);

        Gun result = container.getUnassignedItem(itemStack);

        assertEquals(gun, result);
    }

    @Test
    public void shouldReturnNullWhenFindingNonMatchingItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        container.addUnassignedItem(gun);

        Gun result = container.getUnassignedItem(itemStack);

        assertNull(result);
    }

    @Test
    public void shouldFindItemByItsCorrespondingItemStackAndHolder() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        GunHolder holder = mock(GunHolder.class);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        container.addAssignedItem(gun, holder);

        Gun result = container.getAssignedItem(holder, itemStack);

        assertEquals(gun, result);
    }
    
    @Test
    public void shouldReturnNullWhenHolderIsNotInStorage() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        
        GunHolder holder = mock(GunHolder.class);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        Gun result = container.getAssignedItem(holder, itemStack);
        
        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenHolderDoesNotHaveItemStack() {
        ItemStack notAdded = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        GunHolder holder = mock(GunHolder.class);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        container.addAssignedItem(gun, holder);

        Gun result = container.getAssignedItem(holder, notAdded);

        assertNull(result);
    }

    @Test
    public void getAssignedItemsReturnsEmptyListIfInstanceDoesNotContainRecordWithGivenHolder() {
        GunHolder holder = mock(GunHolder.class);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        List<Gun> assignedGuns = container.getAssignedItems(holder);

        assertEquals(0, assignedGuns.size());
    }

    @Test
    public void getAssignedItemsReturnsListOfAssignedItemsToTheHolder() {
        Gun gun = mock(Gun.class);
        GunHolder holder = mock(GunHolder.class);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        container.addAssignedItem(gun, holder);

        List<Gun> assignedGuns = container.getAssignedItems(holder);

        assertEquals(1, assignedGuns.size());
        assertEquals(gun, assignedGuns.get(0));
    }

    @Test
    public void shouldRemoveItemFromStorage() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        container.addUnassignedItem(gun);
        container.removeUnassignedItem(gun);

        Gun result = container.getUnassignedItem(itemStack);

        assertNull(result);
    }

    @Test
    public void shouldRemoveItemFromHolderIfBothRegistered() {
        Gun gun = mock(Gun.class);

        GunHolder holder = mock(GunHolder.class);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        container.addAssignedItem(gun, holder);

        boolean result = container.removeAssignedItem(gun, holder);

        assertTrue(result);
    }

    @Test
    public void shouldNotRemoveItemFromHolderIfStorageDoesNotContainHolder() {
        Gun gun = mock(Gun.class);

        GunHolder holder = mock(GunHolder.class);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        boolean result = container.removeAssignedItem(gun, holder);

        assertFalse(result);
    }

    @Test
    public void shouldNotRemoveItemFromHolderIfTheyDoNotHaveAny() {
        Gun gun = mock(Gun.class);

        GunHolder holder = mock(GunHolder.class);

        ItemContainer<Gun, GunHolder> container = new ItemContainer<>();
        container.addAssignedItem(mock(Gun.class), holder);

        boolean result = container.removeAssignedItem(gun, holder);

        assertFalse(result);
    }
}
