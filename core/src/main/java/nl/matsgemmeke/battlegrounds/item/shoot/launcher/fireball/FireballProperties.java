package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import org.jspecify.annotations.Nullable;

public record FireballProperties(
        @Nullable
        SoundTemplate launchSoundTemplate,
        @Nullable
        ParticleEffect trajectoryParticleEffect,
        double velocity
) {
}
