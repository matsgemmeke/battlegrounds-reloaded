package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record HitscanProperties(
        List<GameSound> launchSounds,
        @Nullable ParticleEffect trajectoryParticleEffect
) { }
