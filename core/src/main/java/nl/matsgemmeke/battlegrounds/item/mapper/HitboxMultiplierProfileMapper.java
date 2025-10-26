package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.configuration.item.HitboxMultiplierSpec;
import nl.matsgemmeke.battlegrounds.item.effect.damage.HitboxMultiplierProfile;

public class HitboxMultiplierProfileMapper {

    public HitboxMultiplierProfile map(HitboxMultiplierSpec spec) {
        double headshotDamageMultiplier = spec.head;
        double bodyDamageMultiplier = spec.body;
        double legsDamageMultiplier = spec.legs;

        return new HitboxMultiplierProfile(headshotDamageMultiplier, bodyDamageMultiplier, legsDamageMultiplier);
    }
}
