package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class SoundNotificationEffectSpec extends ItemEffectSpec {

    @Required
    public String notificationSounds;
}
