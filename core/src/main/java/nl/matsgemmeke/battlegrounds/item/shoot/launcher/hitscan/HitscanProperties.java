package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.jetbrains.annotations.Nullable;

public record HitscanProperties(
        @Nullable
        SoundSequence soundSequence,
        @Nullable
        ParticleEffect trajectoryParticleEffect
) { }
