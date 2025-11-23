package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.item.PotionEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

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
    public PotionEffectSpec potionEffect;
}
