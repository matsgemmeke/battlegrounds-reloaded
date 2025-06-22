package nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;

import java.util.List;

public record BulletProperties(
        ParticleEffect trajectoryParticleEffect,
        List<GameSound> shotSounds
) { }
