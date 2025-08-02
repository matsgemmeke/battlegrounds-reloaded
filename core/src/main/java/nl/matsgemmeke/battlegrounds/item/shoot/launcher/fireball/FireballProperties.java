package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;

import java.util.List;
import java.util.Optional;

public record FireballProperties(
        List<GameSound> shotSounds,
        ParticleEffect trajectoryParticleEffect,
        double velocity
) {
    public Optional<ParticleEffect> getTrajectoryParticleEffect() {
        return Optional.ofNullable(trajectoryParticleEffect);
    }
}
