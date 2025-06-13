package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class DefaultGunRegistryTest {

    private ItemContainer<Gun, GunHolder> gunContainer;

    @BeforeEach
    public void setUp() {
        gunContainer = new ItemContainer<>();
    }

    @Test
    public void findAllReturnsAllGunItemsFromContainer() {
        Gun gun = mock(Gun.class);
        gunContainer.addUnassignedItem(gun);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunContainer);
        List<Gun> guns = gunRegistry.findAll();

        assertEquals(1, guns.size());
        assertEquals(gun, guns.get(0));
    }

    @Test
    public void getAssignedItemsReturnsAssignedItemsFromContainer() {
        Gun gun = mock(Gun.class);
        GunHolder holder = mock(GunHolder.class);

        gunContainer.addAssignedItem(gun, holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunContainer);
        List<Gun> assignedItems = gunRegistry.getAssignedItems(holder);

        assertThat(assignedItems).containsExactly(gun);
    }

    @Test
    public void registerItemRegistersUnassignedGunToContainer() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunContainer);
        gunRegistry.registerItem(gun);

        assertEquals(gun, gunContainer.getUnassignedItem(itemStack));
    }

    @Test
    public void registerItemRegistersAssignedGunToContainer() {
        GunHolder holder = mock(GunHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunContainer);
        gunRegistry.registerItem(gun, holder);

        assertEquals(gun, gunContainer.getAssignedItem(holder, itemStack));
    }

    @Test
    public void unassignItemDoesNothingIfGivenGunHasNoHolder() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(null);
        when(gun.isMatching(itemStack)).thenReturn(true);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunContainer);
        gunRegistry.unassignItem(gun);

        assertNull(gunContainer.getUnassignedItem(itemStack));
    }

    @Test
    public void unassignItemRemovesGunFromAssignedListAndAddsGunToUnassignedList() {
        GunHolder holder = mock(GunHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(holder);
        when(gun.isMatching(itemStack)).thenReturn(true);

        gunContainer.addAssignedItem(gun, holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunContainer);
        gunRegistry.unassignItem(gun);

        assertNull(gunContainer.getAssignedItem(holder, itemStack));
        assertEquals(gun, gunContainer.getUnassignedItem(itemStack));
    }
}
