package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

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
    @Valid
    public ParticleEffectSpec particleEffect;
}
