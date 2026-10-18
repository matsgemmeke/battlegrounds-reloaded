package nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect;

import jakarta.validation.constraints.NotBlank;
import nl.matsgemmeke.battlegrounds.item.validation.constraint.ValidSoundTemplate;

public class StickEffectSpec extends ProjectileEffectSpec {

    @NotBlank(message = "sound sequence id is required")
    @ValidSoundTemplate
    public String soundSequence;
}
