package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultMeleeWeaponRegistryTest {

    private DefaultMeleeWeaponRegistry meleeWeaponRegistry;

    @BeforeEach
    void setUp() {
        meleeWeaponRegistry = new DefaultMeleeWeaponRegistry();
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
}
