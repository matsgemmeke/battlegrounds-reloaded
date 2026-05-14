package nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class SoundEffectSpec extends ProjectileEffectSpec {

    @Required
    public String sounds;
}
