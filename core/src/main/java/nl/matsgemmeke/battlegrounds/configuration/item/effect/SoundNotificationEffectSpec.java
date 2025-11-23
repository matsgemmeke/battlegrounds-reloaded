package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class SoundNotificationEffectSpec extends ItemEffectSpec {

    @Required
    public String notificationSounds;
}
