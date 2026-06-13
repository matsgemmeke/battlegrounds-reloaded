package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record FireballProperties(
        List<GameSound> launchSounds,
        @Nullable
        ParticleEffect trajectoryParticleEffect,
        double velocity
) {
    public Optional<ParticleEffect> getTrajectoryParticleEffect() {
        return Optional.ofNullable(trajectoryParticleEffect);
    }
}
