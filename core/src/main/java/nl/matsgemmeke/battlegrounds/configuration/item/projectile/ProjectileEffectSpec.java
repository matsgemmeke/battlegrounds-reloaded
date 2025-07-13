package nl.matsgemmeke.battlegrounds.configuration.item.projectile;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.ConditionalRequired;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

import java.util.Map;

public class ProjectileEffectSpec {

    @Required
    @EnumValue(type = ProjectileEffectType.class)
    public String type;
    public Map<String, TriggerSpec> triggers;
    public String sounds;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "BOUNCE")
    public Double horizontalFriction;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "BOUNCE")
    public Double verticalFriction;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "BOUNCE", "TRAIL" })
    public Integer maxActivations;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "TRAIL")
    public ParticleEffectSpec particleEffect;

    private enum ProjectileEffectType {
        BOUNCE, SOUND, STICK, TRAIL
    }
}
