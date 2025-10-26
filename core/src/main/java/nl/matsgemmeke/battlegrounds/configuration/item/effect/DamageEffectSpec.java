package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class DamageEffectSpec extends ItemEffectSpec {

    @Required
    @EnumValue(type = DamageType.class)
    public String damageType;

    @Required
    public RangeProfileSpec range;

    private enum DamageType {
        ATTACK_DAMAGE, BULLET_DAMAGE, ENVIRONMENTAL_DAMAGE, EXPLOSIVE_DAMAGE, EXPLOSIVE_ITEM_DAMAGE, FIRE_DAMAGE
    }
}
