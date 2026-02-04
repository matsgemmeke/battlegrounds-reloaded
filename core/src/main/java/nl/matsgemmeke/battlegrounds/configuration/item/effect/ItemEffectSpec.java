package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public abstract class ItemEffectSpec {

    @Required
    @EnumValue(type = ItemEffectType.class)
    public String effectType;

    private enum ItemEffectType {
        COMBUSTION, DAMAGE, EXPLOSION, FLASH, GUN_FIRE_SIMULATION, MARK_SPAWN_POINT, SMOKE_SCREEN, SOUND_NOTIFICATION
    }
}
