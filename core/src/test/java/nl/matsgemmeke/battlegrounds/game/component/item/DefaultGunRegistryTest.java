package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

public class DefaultGunRegistryTest {

    @Test
    public void assignDoesNothingWhenGivenGunIsNotRegistered() {
        GunHolder holder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.assign(gun, holder);

        assertThat(gunRegistry.getAssignedGuns(holder)).isEmpty();
    }

    @Test
    public void assignAddsGivenGunToAssignedListOfGivenHolder() {
        GunHolder holder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.register(gun);
        gunRegistry.assign(gun, holder);

        assertThat(gunRegistry.getAssignedGuns(holder)).containsExactly(gun);
    }


    @Test
    public void unassignDoesNothingWhenGivenGunHasNoHolder() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(null);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();

        // Add better assertions once more methods are exposed
        assertThatCode(() -> gunRegistry.unassign(gun)).doesNotThrowAnyException();
    }

    @Test
    public void unassignDoesNothingWhenGivenGunHolderIsNotRegistered() {
        GunHolder holder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.unassign(gun);

        assertThat(gunRegistry.getAssignedGuns(holder)).isEmpty();
    }

    @Test
    public void unassignRemovesGivenGunFromGivenHolder() {
        GunHolder holder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.register(gun, holder);
        gunRegistry.unassign(gun);

        assertThat(gunRegistry.getAssignedGuns(holder)).isEmpty();
    }

    @Test
    public void getAssignedGunReturnsEmptyOptionalWhenNoAssignedGunsOfGivenHolderMatch() {
        GunHolder holder = mock(GunHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(false);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.register(gun, holder);
        Optional<Gun> gunOptional = gunRegistry.getAssignedGun(holder, itemStack);

        assertThat(gunOptional).isEmpty();
    }

    @Test
    public void getAssignedGunReturnsOptionalWithMatchingGunAssignedToGivenHolder() {
        GunHolder holder = mock(GunHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.register(gun, holder);
        Optional<Gun> gunOptional = gunRegistry.getAssignedGun(holder, itemStack);

        assertThat(gunOptional).hasValue(gun);
    }

    @Test
    public void getAssignedGunsReturnsAssignedItemsForGivenHolder() {
        Gun gun = mock(Gun.class);
        GunHolder holder = mock(GunHolder.class);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.register(gun, holder);
        List<Gun> assignedItems = gunRegistry.getAssignedGuns(holder);

        assertThat(assignedItems).containsExactly(gun);
    }

    @Test
    public void getUnassignedGunReturnsEmptyOptionalWhenNoUnassignedGunsMatch() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(false);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.register(gun);
        Optional<Gun> gunOptional = gunRegistry.getUnassignedGun(itemStack);

        assertThat(gunOptional).isEmpty();
    }

    @Test
    public void getUnassignedGunReturnsOptionalWithMatchingUnassignedGun() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.register(gun);
        Optional<Gun> gunOptional = gunRegistry.getUnassignedGun(itemStack);

        assertThat(gunOptional).hasValue(gun);
    }
}
