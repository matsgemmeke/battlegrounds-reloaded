package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.item.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.ConditionalRequired;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ProjectileSpec {

    @Required
    @EnumValue(type = ProjectileType.class)
    public String type;
    @Required
    public ItemEffectSpec effect;
    public ParticleEffectSpec trajectoryParticleEffect;
    public Double headshotDamageMultiplier;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "FIREBALL")
    public Double velocity;
    public String shotSounds;
    public String suppressedShotSounds;

    private enum ProjectileType {
        BULLET, FIREBALL
    }
}
