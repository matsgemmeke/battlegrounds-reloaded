package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class ExplosionEffectSpec extends ItemEffectSpec {

    @Required
    @Valid
    public RangeProfileSpec range;

    @Required
    public Float power;

    @Required
    public Boolean damageBlocks;

    @Required
    public Boolean spreadFire;
}
