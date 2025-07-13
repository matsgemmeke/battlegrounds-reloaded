package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProjectileLauncherFactory {

    @NotNull
    private final BulletLauncherFactory bulletLauncherFactory;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;

    @Inject
    public ProjectileLauncherFactory(
            @NotNull BulletLauncherFactory bulletLauncherFactory,
            @NotNull GameContextProvider contextProvider,
            @NotNull ParticleEffectMapper particleEffectMapper
    ) {
        this.bulletLauncherFactory = bulletLauncherFactory;
        this.contextProvider = contextProvider;
        this.particleEffectMapper = particleEffectMapper;
    }

    @NotNull
    public ProjectileLauncher create(@NotNull ProjectileSpec spec, @NotNull GameKey gameKey) {
        ProjectileLauncherType projectileLauncherType = ProjectileLauncherType.valueOf(spec.type);

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
        CollisionDetector collisionDetector = contextProvider.getComponent(gameKey, CollisionDetector.class);

        switch (projectileLauncherType) {
            case BULLET -> {
                List<GameSound> shotSounds = DefaultGameSound.parseSounds(spec.shotSounds);
                ParticleEffect trajectoryParticleEffect = null;
                ParticleEffectSpec trajectoryParticleEffectSpec = spec.trajectoryParticleEffect;

                if (trajectoryParticleEffectSpec != null) {
                    trajectoryParticleEffect = particleEffectMapper.map(trajectoryParticleEffectSpec);
                }

                BulletProperties properties = new BulletProperties(shotSounds, trajectoryParticleEffect);

                return bulletLauncherFactory.create(properties, audioEmitter, collisionDetector);
            }
            default -> throw new ProjectileLauncherCreationException("Invalid projectile launcher type '%s'".formatted(projectileLauncherType));
        }
    }
}
