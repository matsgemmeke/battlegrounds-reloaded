package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.PotionEffectSpec;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class FlashEffectSpec extends ItemEffectSpec {

    @Required
    public Double range;

    @Required
    public Float power;

    @Required
    public Boolean damageBlocks;

    @Required
    public Boolean spreadFire;

    @Required
    @Valid
    public PotionEffectSpec potionEffect;
}
