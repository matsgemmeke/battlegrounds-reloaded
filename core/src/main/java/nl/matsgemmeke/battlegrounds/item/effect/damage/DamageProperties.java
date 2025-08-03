package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;

public record DamageProperties(RangeProfile rangeProfile, DamageType damageType) {
}
