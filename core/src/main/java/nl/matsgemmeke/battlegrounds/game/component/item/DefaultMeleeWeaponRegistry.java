package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultMeleeWeaponRegistry implements MeleeWeaponRegistry {

    private final ConcurrentMap<MeleeWeaponHolder, List<MeleeWeapon>> assignedMeleeWeapons;
    private final List<MeleeWeapon> unassignedMeleeWeapons;

    public DefaultMeleeWeaponRegistry() {
        this.assignedMeleeWeapons = new ConcurrentHashMap<>();
        this.unassignedMeleeWeapons = new ArrayList<>();
    }

    @Override
    public Optional<MeleeWeapon> getAssignedMeleeWeapon(MeleeWeaponHolder holder, ItemStack itemStack) {
        if (!assignedMeleeWeapons.containsKey(holder)) {
            return Optional.empty();
        }

        return assignedMeleeWeapons.get(holder).stream()
                .filter(meleeWeapon -> meleeWeapon.isMatching(itemStack))
                .findFirst();
    }

    @Override
    public void register(MeleeWeapon meleeWeapon) {
        unassignedMeleeWeapons.add(meleeWeapon);
    }

    @Override
    public void register(MeleeWeapon meleeWeapon, MeleeWeaponHolder holder) {
        assignedMeleeWeapons.computeIfAbsent(holder, h -> new ArrayList<>()).add(meleeWeapon);
    }
}
