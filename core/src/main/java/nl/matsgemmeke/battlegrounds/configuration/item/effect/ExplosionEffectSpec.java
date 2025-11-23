package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ExplosionEffectSpec extends ItemEffectSpec {

    @Required
    public RangeProfileSpec range;

    @Required
    public Float power;

    @Required
    public Boolean damageBlocks;

    @Required
    public Boolean spreadFire;
}
