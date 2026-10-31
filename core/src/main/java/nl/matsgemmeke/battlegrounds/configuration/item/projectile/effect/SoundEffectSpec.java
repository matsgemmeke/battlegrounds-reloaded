package nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect;

import jakarta.validation.constraints.NotBlank;

public class SoundEffectSpec extends ProjectileEffectSpec {

    @NotBlank(message = "sound sequence id is required")
    public String soundSequence;
}
