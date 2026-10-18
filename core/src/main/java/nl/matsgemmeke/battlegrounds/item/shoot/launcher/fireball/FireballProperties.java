package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.jspecify.annotations.Nullable;

public record FireballProperties(
        @Nullable
        SoundSequence soundSequence,
        @Nullable
        ParticleEffect trajectoryParticleEffect,
        double velocity
) {
}
