package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.item.HitboxMultiplierSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class DamageEffectSpec extends ItemEffectSpec {

    @Required
    @EnumValue(type = DamageType.class)
    public String damageType;

    public Double radius;

    @Required
    public HitboxMultiplierSpec hitboxMultipliers;

    @Required
    public RangeProfileSpec range;

    private enum DamageType {
        ATTACK_DAMAGE,
        BULLET_DAMAGE,
        ENVIRONMENTAL_DAMAGE,
        EXPLOSIVE_DAMAGE,
        EXPLOSIVE_ITEM_DAMAGE,
        FIRE_DAMAGE,
        PROJECTILE_DAMAGE
    }
}
