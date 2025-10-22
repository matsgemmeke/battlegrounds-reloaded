package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;

public record DamageProperties(HitboxDamageProfile hitboxDamageProfile, RangeProfile rangeProfile, DamageType damageType) {
}
