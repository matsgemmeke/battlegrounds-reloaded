package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ProjectileSpec {

    @Required
    public String type;
    public Double headshotDamageMultiplier;
    public ParticleEffectSpec trajectoryParticleEffect;
}
