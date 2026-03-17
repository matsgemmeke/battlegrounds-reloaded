package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public abstract class ItemEffectSpec {

    @Required
    @EnumValue(type = ItemEffectType.class)
    public String type;

    private enum ItemEffectType {
        COMBUSTION, DAMAGE, EXPLOSION, FLASH, GUN_FIRE_SIMULATION, MARK_SPAWN_POINT, SMOKE_SCREEN, SOUND_NOTIFICATION
    }
}
