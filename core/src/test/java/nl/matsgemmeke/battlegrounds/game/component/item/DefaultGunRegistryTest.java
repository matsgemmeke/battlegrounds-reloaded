package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

class DefaultGunRegistryTest {

    private DefaultGunRegistry gunRegistry;

    @BeforeEach
    void setUp() {
        gunRegistry = new DefaultGunRegistry();
    }

    @Test
    @DisplayName("assign does nothing when given gun is not registered")
    void assign_gunNotRegistered() {
        GunUser user = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(user);

        gunRegistry.assign(gun, user);

        assertThat(gunRegistry.getAssignedGuns(user)).isEmpty();
    }

    @Test
    @DisplayName("assign adds given gun to assigned list of given user")
    void assign_gunRegistered() {
        GunUser user = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(user);

        gunRegistry.register(gun);
        gunRegistry.assign(gun, user);

        assertThat(gunRegistry.getAssignedGuns(user)).containsExactly(gun);
    }

    @Test
    @DisplayName("unassign does nothing when given gun has no user")
    void unassign_gunHasNoUser() {
        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(null);

        // Add better assertions once more methods are exposed
        assertThatCode(() -> gunRegistry.unassign(gun)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("unassign does nothing when given user is not registered")
    void unassign_gunUserNotRegistered() {
        GunUser user = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(user);

        gunRegistry.unassign(gun);

        assertThat(gunRegistry.getAssignedGuns(user)).isEmpty();
    }

    @Test
    @DisplayName("unassign removes given gun from given user")
    void unassign_gunUserRegistered() {
        GunUser user = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(user);

        gunRegistry.register(gun, user);
        gunRegistry.unassign(gun);

        assertThat(gunRegistry.getAssignedGuns(user)).isEmpty();
    }

    @Test
    @DisplayName("getAssignedGun returns empty optional when given user has no guns registered")
    void getAssignedGun_userWithoutRegisteredGuns() {
        GunUser user = mock(GunUser.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Optional<Gun> gunOptional = gunRegistry.getAssignedGun(user, itemStack);

        assertThat(gunOptional).isEmpty();
    }

    @Test
    @DisplayName("getAssignedGun returns empty optional when no assigned guns of given user match")
    void getAssignedGun_noMatchingGuns() {
        GunUser user = mock(GunUser.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(false);

        gunRegistry.register(gun, user);
        Optional<Gun> gunOptional = gunRegistry.getAssignedGun(user, itemStack);

        assertThat(gunOptional).isEmpty();
    }

    @Test
    @DisplayName("getAssignedGun returns optional with matching assigned to given user")
    void getAssignedGun_returnsMatchingGun() {
        GunUser user = mock(GunUser.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        gunRegistry.register(gun, user);
        Optional<Gun> gunOptional = gunRegistry.getAssignedGun(user, itemStack);

        assertThat(gunOptional).hasValue(gun);
    }

    @Test
    @DisplayName("getAssignedGuns returns assigned guns to given user")
    void getAssignedGuns_returnsAssignedGuns() {
        Gun gun = mock(Gun.class);
        GunUser user = mock(GunUser.class);

        gunRegistry.register(gun, user);
        List<Gun> assignedItems = gunRegistry.getAssignedGuns(user);

        assertThat(assignedItems).containsExactly(gun);
    }

    @Test
    @DisplayName("getUnassignedGuns returns empty optional when none of the unassigned guns match")
    void getUnassignedGun_noMatches() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(false);

        gunRegistry.register(gun);
        Optional<Gun> gunOptional = gunRegistry.getUnassignedGun(itemStack);

        assertThat(gunOptional).isEmpty();
    }

    @Test
    @DisplayName("getUnassignedGuns returns optional with matching unassigned gun")
    void getUnassignedGun_returnsMatchingGun() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Gun gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        gunRegistry.register(gun);
        Optional<Gun> gunOptional = gunRegistry.getUnassignedGun(itemStack);

        assertThat(gunOptional).hasValue(gun);
    }
}
