package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class SmokeScreenEffectSpec extends ItemEffectSpec {

    public String activationSounds;

    @Required
    public Double minSize;

    @Required
    public Double maxSize;

    @Required
    public Double density;

    @Required
    public Double growth;

    @Required
    public Long growthInterval;

    @Required
    public Long minDuration;

    @Required
    public Long maxDuration;

    @Required
    public ParticleEffectSpec particleEffect;
}
