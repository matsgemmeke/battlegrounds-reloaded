package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultMeleeWeaponRegistry implements MeleeWeaponRegistry {

    private final List<MeleeWeapon> meleeWeapons;

    public DefaultMeleeWeaponRegistry() {
        this.meleeWeapons = new ArrayList<>();
    }

    @Override
    public Optional<MeleeWeapon> getAssignedMeleeWeapon(MeleeWeaponUser user, ItemStack itemStack) {
        return meleeWeapons.stream()
                .filter(meleeWeapon -> meleeWeapon.getUser().map(h -> h.equals(user)).orElse(false))
                .filter(meleeWeapon -> meleeWeapon.isMatching(itemStack))
                .findFirst();
    }

    @Override
    public List<MeleeWeapon> getAssignedMeleeWeapons(MeleeWeaponUser user) {
        return meleeWeapons.stream()
                .filter(meleeWeapon -> meleeWeapon.getUser().stream().anyMatch(h -> h.equals(user) && h.hasItem(meleeWeapon)))
                .toList();
    }

    @Override
    public Optional<MeleeWeapon> getUnassignedMeleeWeapon(ItemStack itemStack) {
        return meleeWeapons.stream()
                .filter(meleeWeapon -> meleeWeapon.getUser().isEmpty())
                .filter(meleeWeapon -> meleeWeapon.isMatching(itemStack))
                .findFirst();
    }

    @Override
    public void register(MeleeWeapon meleeWeapon) {
        meleeWeapons.add(meleeWeapon);
    }
}
