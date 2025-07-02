package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ProjectileSpec {

    @Required
    @EnumValue(type = ProjectileType.class)
    public String type;
    public String shotSounds;
    public String suppressedShotSounds;
    public Double headshotDamageMultiplier;
    public ParticleEffectSpec trajectoryParticleEffect;

    private enum ProjectileType {
        BULLET
    }
}
