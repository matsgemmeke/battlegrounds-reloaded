package nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class SoundEffectSpec extends ProjectileEffectSpec {

    @Required
    public String sounds;
}
