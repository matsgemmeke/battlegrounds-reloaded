package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;

public record DamageProperties(
        DamageType damageType,
        RangeProfile rangeProfile,
        HitboxMultiplierProfile hitboxMultiplierProfile,
        double radius
) {
}
