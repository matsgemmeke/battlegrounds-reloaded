package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.HitboxDamageProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class DamageEffectSpec extends ItemEffectSpec {

    @Required
    @EnumValue(type = DamageType.class)
    public String damageType;

    @Required
    @Valid
    public RangeProfileSpec range;

    @Required
    @Valid
    public HitboxDamageProfileSpec hitboxDamageProfile;

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
