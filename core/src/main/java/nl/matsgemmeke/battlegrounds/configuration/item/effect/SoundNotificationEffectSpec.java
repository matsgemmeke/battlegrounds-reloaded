package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import jakarta.validation.constraints.NotNull;

public class SoundNotificationEffectSpec extends ItemEffectSpec {

    @NotNull
    public String soundTemplate;
}
