package nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BulletProperties(
        @NotNull List<GameSound> shotSounds,
        @Nullable ParticleEffect trajectoryParticleEffect
) { }
