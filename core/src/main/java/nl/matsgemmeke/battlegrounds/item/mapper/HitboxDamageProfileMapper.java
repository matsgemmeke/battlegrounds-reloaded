package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.item.effect.damage.HitboxDamageProfile;
import nl.matsgemmeke.battlegrounds.configuration.item.HitboxDamageProfileSpec;

public class HitboxDamageProfileMapper {

    public HitboxDamageProfile map(HitboxDamageProfileSpec spec) {
        double headDamageModifier = spec.head;
        double torsoDamageModifier = spec.torso;
        double limbsDamageModifier = spec.limbs;

        return new HitboxDamageProfile(headDamageModifier, torsoDamageModifier, limbsDamageModifier);
    }
}
