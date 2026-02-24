package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultMeleeWeaponRegistryTest {

    private DefaultMeleeWeaponRegistry meleeWeaponRegistry;

    @BeforeEach
    void setUp() {
        meleeWeaponRegistry = new DefaultMeleeWeaponRegistry();
    }

    @Test
    void assignDoesNothingWhenGivenMeleeWeaponIsNotRegistered() {
        MeleeWeaponHolder holder = mock(MeleeWeaponHolder.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(holder));

        meleeWeaponRegistry.assign(meleeWeapon, holder);

        assertThat(meleeWeaponRegistry.getAssignedMeleeWeapons(holder)).isEmpty();
    }

    @Test
    void assignAddsGivenMeleeWeaponToAssignedListOfGivenHolder() {
        MeleeWeaponHolder holder = mock(MeleeWeaponHolder.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(holder));

        meleeWeaponRegistry.register(meleeWeapon);
        meleeWeaponRegistry.assign(meleeWeapon, holder);

        assertThat(meleeWeaponRegistry.getAssignedMeleeWeapons(holder)).containsExactly(meleeWeapon);
    }


    @Test
    void unassignDoesNothingWhenGivenMeleeWeaponHasNoHolder() {
        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.empty());

        // Add better assertions once more methods are exposed
        assertThatCode(() -> meleeWeaponRegistry.unassign(meleeWeapon)).doesNotThrowAnyException();
    }

    @Test
    void unassignDoesNothingWhenGivenMeleeWeaponHolderIsNotRegistered() {
        MeleeWeaponHolder holder = mock(MeleeWeaponHolder.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(holder));

        meleeWeaponRegistry.unassign(meleeWeapon);

        assertThat(meleeWeaponRegistry.getAssignedMeleeWeapons(holder)).isEmpty();
    }

    @Test
    void unassignRemovesGivenMeleeWeaponFromGivenHolder() {
        MeleeWeaponHolder holder = mock(MeleeWeaponHolder.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(holder));

        meleeWeaponRegistry.register(meleeWeapon, holder);
        meleeWeaponRegistry.unassign(meleeWeapon);

        assertThat(meleeWeaponRegistry.getAssignedMeleeWeapons(holder)).isEmpty();
    }

    @Test
    void getAssignedMeleeWeaponReturnsEmptyOptionalWhenGivenHolderIsNotRegistered() {
        MeleeWeaponHolder holder = mock(MeleeWeaponHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);

        Optional<MeleeWeapon> meleeWeaponOptional = meleeWeaponRegistry.getAssignedMeleeWeapon(holder, itemStack);

        assertThat(meleeWeaponOptional).isEmpty();
    }

    @Test
    void getAssignedMeleeWeaponReturnsEmptyOptionalWhenGivenItemStackIsNotRegisteredToGivenHolder() {
        MeleeWeaponHolder holder = mock(MeleeWeaponHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.isMatching(itemStack)).thenReturn(false);

        meleeWeaponRegistry.register(meleeWeapon, holder);
        Optional<MeleeWeapon> meleeWeaponOptional = meleeWeaponRegistry.getAssignedMeleeWeapon(holder, itemStack);

        assertThat(meleeWeaponOptional).isEmpty();
    }

    @Test
    void getAssignedMeleeWeaponReturnsOptionalWithMeleeWeaponWhenGivenItemStackMatchesWithRegisteredMeleeWeapon() {
        MeleeWeaponHolder holder = mock(MeleeWeaponHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.isMatching(itemStack)).thenReturn(true);

        meleeWeaponRegistry.register(meleeWeapon, holder);
        Optional<MeleeWeapon> meleeWeaponOptional = meleeWeaponRegistry.getAssignedMeleeWeapon(holder, itemStack);

        assertThat(meleeWeaponOptional).hasValue(meleeWeapon);
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
