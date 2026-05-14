package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultMeleeWeaponRegistryTest {

    @Mock
    private MeleeWeaponUser user;

    private DefaultMeleeWeaponRegistry meleeWeaponRegistry;

    @BeforeEach
    void setUp() {
        meleeWeaponRegistry = new DefaultMeleeWeaponRegistry();
    }

    @Test
    @DisplayName("getAssignedMeleeWeapon returns empty optional when given user is not registered")
    void getAssignedMeleeWeapon_userNotRegistered() {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);

        Optional<MeleeWeapon> meleeWeaponOptional = meleeWeaponRegistry.getAssignedMeleeWeapon(user, itemStack);

        assertThat(meleeWeaponOptional).isEmpty();
    }

    @Test
    @DisplayName("getAssignedMeleeWeapon returns empty optional when melee weapon given item stack is not registered to given user")
    void getAssignedMeleeWeapon_meleeWeaponNotRegisteredToUser() {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.of(user));
        when(meleeWeapon.isMatching(itemStack)).thenReturn(false);

        meleeWeaponRegistry.register(meleeWeapon);
        Optional<MeleeWeapon> meleeWeaponOptional = meleeWeaponRegistry.getAssignedMeleeWeapon(user, itemStack);

        assertThat(meleeWeaponOptional).isEmpty();
    }

    @Test
    void getAssignedMeleeWeaponReturnsOptionalWithMeleeWeaponWhenGivenItemStackMatchesWithRegisteredMeleeWeapon() {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.of(user));
        when(meleeWeapon.isMatching(itemStack)).thenReturn(true);

        meleeWeaponRegistry.register(meleeWeapon);
        Optional<MeleeWeapon> meleeWeaponOptional = meleeWeaponRegistry.getAssignedMeleeWeapon(user, itemStack);

        assertThat(meleeWeaponOptional).hasValue(meleeWeapon);
    }

    @Test
    @DisplayName("getAssignedMeleeWeapons returns list of melee weapons that are assigned to the given user and exist in their inventory")
    void getAssignedMeleeWeapons_returnsMatchingMeleeWeapons() {
        MeleeWeapon meleeWeaponDifferentUser = mock(MeleeWeapon.class);

        MeleeWeapon meleeWeaponNotInInventory = mock(MeleeWeapon.class);
        when(meleeWeaponNotInInventory.getUser()).thenReturn(Optional.of(user));

        MeleeWeapon meleeWeaponMatching = mock(MeleeWeapon.class);
        when(meleeWeaponMatching.getUser()).thenReturn(Optional.of(user));

        when(user.hasItem(meleeWeaponNotInInventory)).thenReturn(false);
        when(user.hasItem(meleeWeaponMatching)).thenReturn(true);

        meleeWeaponRegistry.register(meleeWeaponDifferentUser);
        meleeWeaponRegistry.register(meleeWeaponNotInInventory);
        meleeWeaponRegistry.register(meleeWeaponMatching);
        List<MeleeWeapon> meleeWeapons = meleeWeaponRegistry.getAssignedMeleeWeapons(user);

        assertThat(meleeWeapons).containsExactly(meleeWeaponMatching);
    }

    @Test
    @DisplayName("getUnassignedMeleeWeapon returns empty optional when no unassigned melee weapon matches given item stack")
    void getUnassignedMeleeWeapon_noMatchingMeleeWeapon() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.isMatching(itemStack)).thenReturn(false);

        meleeWeaponRegistry.register(meleeWeapon);
        Optional<MeleeWeapon> meleeWeaponOptional = meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack);

        assertThat(meleeWeaponOptional).isEmpty();
    }

    @Test
    @DisplayName("getUnassignedMeleeWeapon returns optional with matching unassigned melee weapon")
    void getUnassignedMeleeWeapon_matchingMeleeWeapon() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.isMatching(itemStack)).thenReturn(true);

        meleeWeaponRegistry.register(meleeWeapon);
        Optional<MeleeWeapon> meleeWeaponOptional = meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack);

        assertThat(meleeWeaponOptional).hasValue(meleeWeapon);
    }
}
