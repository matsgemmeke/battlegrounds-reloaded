package nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.TriggerSpec;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

import java.util.HashMap;
import java.util.Map;

public abstract class ProjectileEffectSpec {

    @Required
    @EnumValue(type = ProjectileEffectType.class)
    public String type;

    public Map<String, @Valid TriggerSpec> triggers = new HashMap<>();
}
