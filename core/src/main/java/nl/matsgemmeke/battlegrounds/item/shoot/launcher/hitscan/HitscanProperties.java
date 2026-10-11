package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import org.jetbrains.annotations.Nullable;

public record HitscanProperties(
        @Nullable
        SoundTemplate launchSoundTemplate,
        @Nullable
        ParticleEffect trajectoryParticleEffect
) { }
