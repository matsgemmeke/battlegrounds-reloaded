package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class CombustionEffectSpec extends ItemEffectSpec {

    @Required
    public Double growth;

    public String activationSounds;

    @Required
    public RangeProfileSpec range;

    @Required
    public Double minSize;

    @Required
    public Double maxSize;

    @Required
    public Long growthInterval;

    @Required
    public Long minDuration;

    @Required
    public Long maxDuration;

    @Required
    public Boolean damageBlocks;

    @Required
    public Boolean spreadFire;
}
