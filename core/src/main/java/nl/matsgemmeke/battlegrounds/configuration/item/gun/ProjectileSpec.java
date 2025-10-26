package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.ConditionalRequired;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

import java.util.Map;

public class ProjectileSpec {

    @Required
    @EnumValue(type = ProjectileType.class)
    public String type;
    @Required
    public ItemEffectSpec effect;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "ITEM")
    public ItemSpec item;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "FIREBALL")
    public Double velocity;
    public ParticleEffectSpec trajectoryParticleEffect;
    public Double headshotDamageMultiplier;
    public String shotSounds;
    public String suppressedShotSounds;
    public Map<String, TriggerSpec> triggers;

    private enum ProjectileType {
        FIREBALL, HITSCAN, ITEM
    }
}
