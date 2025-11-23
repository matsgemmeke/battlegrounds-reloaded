package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.item.*;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

import java.util.HashMap;
import java.util.Map;

public abstract class ItemEffectSpec {

    @Required
    @EnumValue(type = ItemEffectType.class)
    public String effectType;

    public Map<String, TriggerSpec> triggers = new HashMap<>();

    private enum ItemEffectType {
        COMBUSTION, DAMAGE, EXPLOSION, FLASH, GUN_FIRE_SIMULATION, MARK_SPAWN_POINT, SMOKE_SCREEN, SOUND_NOTIFICATION
    }
}
