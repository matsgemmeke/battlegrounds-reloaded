package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class DefaultGunRegistryTest {

    private ItemStorage<Gun, GunHolder> gunStorage;

    @BeforeEach
    public void setUp() {
        gunStorage = new ItemStorage<>();
    }

    @Test
    public void registerItemRegistersUnassignedGunToStorage() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunStorage);
        gunRegistry.registerItem(gun);

        assertEquals(gun, gunStorage.getUnassignedItem(itemStack));
    }

    @Test
    public void registerItemRegistersAssignedGunToStorage() {
        GunHolder holder = mock(GunHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunStorage);
        gunRegistry.registerItem(gun, holder);

        assertEquals(gun, gunStorage.getAssignedItem(holder, itemStack));
    }

    @Test
    public void unassignItemDoesNothingIfGivenGunHasNoHolder() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(null);
        when(gun.isMatching(itemStack)).thenReturn(true);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunStorage);
        gunRegistry.unassignItem(gun);

        assertNull(gunStorage.getUnassignedItem(itemStack));
    }

    @Test
    public void unassignItemRemovesGunFromAssignedListAndAddsGunToUnassignedList() {
        GunHolder holder = mock(GunHolder.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(holder);
        when(gun.isMatching(itemStack)).thenReturn(true);

        gunStorage.addAssignedItem(gun, holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunStorage);
        gunRegistry.unassignItem(gun);

        assertNull(gunStorage.getAssignedItem(holder, itemStack));
        assertEquals(gun, gunStorage.getUnassignedItem(itemStack));
    }
}
