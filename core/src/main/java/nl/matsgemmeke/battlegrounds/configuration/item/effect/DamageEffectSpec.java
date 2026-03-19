package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.HitboxMultiplierSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class DamageEffectSpec extends ItemEffectSpec {

    @Required
    @EnumValue(type = DamageType.class)
    public String damageType;

    @Required
    @Valid
    public RangeProfileSpec range;

    @Required
    @Valid
    public HitboxMultiplierSpec hitboxMultipliers;

    private enum DamageType {
        BULLET_DAMAGE,
        ENVIRONMENTAL_DAMAGE,
        EXPLOSIVE_DAMAGE,
        EXPLOSIVE_ITEM_DAMAGE,
        FIRE_DAMAGE,
        MELEE_DAMAGE,
        PROJECTILE_DAMAGE
    }
}
