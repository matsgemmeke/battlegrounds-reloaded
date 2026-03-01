package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
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
    public Optional<MeleeWeapon> getAssignedMeleeWeapon(MeleeWeaponHolder holder, ItemStack itemStack) {
        return meleeWeapons.stream()
                .filter(meleeWeapon -> meleeWeapon.getHolder().map(h -> h.equals(holder)).orElse(false))
                .filter(meleeWeapon -> meleeWeapon.isMatching(itemStack))
                .findFirst();
    }

    @Override
    public List<MeleeWeapon> getAssignedMeleeWeapons(MeleeWeaponHolder holder) {
        return meleeWeapons.stream()
                .filter(meleeWeapon -> meleeWeapon.getHolder().map(h -> h.equals(holder)).orElse(false))
                .toList();
    }

    @Override
    public Optional<MeleeWeapon> getUnassignedMeleeWeapon(ItemStack itemStack) {
        return meleeWeapons.stream()
                .filter(meleeWeapon -> meleeWeapon.getHolder().isEmpty())
                .filter(meleeWeapon -> meleeWeapon.isMatching(itemStack))
                .findFirst();
    }

    @Override
    public void register(MeleeWeapon meleeWeapon) {
        meleeWeapons.add(meleeWeapon);
    }
}
