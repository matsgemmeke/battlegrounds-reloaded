package nl.matsgemmeke.battlegrounds.item.melee;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;

public class MeleeWeaponFactory {

    private final MeleeWeaponRegistry meleeWeaponRegistry;

    @Inject
    public MeleeWeaponFactory(MeleeWeaponRegistry meleeWeaponRegistry) {
        this.meleeWeaponRegistry = meleeWeaponRegistry;
    }

    public MeleeWeapon create(MeleeWeaponSpec spec) {
        MeleeWeapon meleeWeapon = this.createInstance(spec);

        meleeWeaponRegistry.register(meleeWeapon);

        return meleeWeapon;
    }

    public MeleeWeapon create(MeleeWeaponSpec spec, MeleeWeaponHolder holder) {
        MeleeWeapon meleeWeapon = this.createInstance(spec);
        meleeWeapon.setHolder(holder);

        meleeWeaponRegistry.register(meleeWeapon, holder);

        return meleeWeapon;
    }

    private MeleeWeapon createInstance(MeleeWeaponSpec spec) {
        DefaultMeleeWeapon meleeWeapon = new DefaultMeleeWeapon(new ItemControls<>());
        meleeWeapon.setName(spec.name);
        meleeWeapon.setDescription(spec.description);

        return meleeWeapon;
    }
}
