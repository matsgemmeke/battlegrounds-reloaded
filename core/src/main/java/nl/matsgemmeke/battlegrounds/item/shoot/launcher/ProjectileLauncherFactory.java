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
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.Effect;
import nl.matsgemmeke.battlegrounds.item.effect.EffectFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternCreationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProjectileLauncherFactory {

    @NotNull
    private final BulletLauncherFactory bulletLauncherFactory;
    @NotNull
    private final FireballLauncherFactory fireballLauncherFactory;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final EffectFactory effectFactory;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;

    @Inject
    public ProjectileLauncherFactory(
            @NotNull BulletLauncherFactory bulletLauncherFactory,
            @NotNull FireballLauncherFactory fireballLauncherFactory,
            @NotNull GameContextProvider contextProvider,
            @NotNull EffectFactory effectFactory,
            @NotNull ParticleEffectMapper particleEffectMapper
    ) {
        this.bulletLauncherFactory = bulletLauncherFactory;
        this.fireballLauncherFactory = fireballLauncherFactory;
        this.contextProvider = contextProvider;
        this.effectFactory = effectFactory;
        this.particleEffectMapper = particleEffectMapper;
    }

    @NotNull
    public ProjectileLauncher create(@NotNull ProjectileSpec spec, @NotNull GameKey gameKey) {
        ProjectileLauncherType projectileLauncherType = ProjectileLauncherType.valueOf(spec.type);

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
        CollisionDetector collisionDetector = contextProvider.getComponent(gameKey, CollisionDetector.class);
        TargetFinder targetFinder = contextProvider.getComponent(gameKey, TargetFinder.class);

        switch (projectileLauncherType) {
            case FIREBALL -> {
                List<GameSound> shotSounds = DefaultGameSound.parseSounds(spec.shotSounds);
                ParticleEffect trajectoryParticleEffect = this.createParticleEffect(spec.trajectoryParticleEffect);
                double velocity = this.validateSpecVar(spec.velocity, "velocity", projectileLauncherType);

                FireballProperties properties = new FireballProperties(shotSounds, trajectoryParticleEffect, velocity);
                Effect effect = effectFactory.create(spec.effect, gameKey);

                return fireballLauncherFactory.create(properties, audioEmitter, collisionDetector, effect, targetFinder);
            }
            case BULLET -> {
                List<GameSound> shotSounds = DefaultGameSound.parseSounds(spec.shotSounds);
                ParticleEffect trajectoryParticleEffect = null;
                ParticleEffectSpec trajectoryParticleEffectSpec = spec.trajectoryParticleEffect;

                if (trajectoryParticleEffectSpec != null) {
                    trajectoryParticleEffect = particleEffectMapper.map(trajectoryParticleEffectSpec);
                }

                BulletProperties properties = new BulletProperties(shotSounds, trajectoryParticleEffect);
                Effect effect = effectFactory.create(spec.effect, gameKey);

                return bulletLauncherFactory.create(properties, audioEmitter, collisionDetector, effect, targetFinder);
            }
            default -> throw new ProjectileLauncherCreationException("Invalid projectile launcher type '%s'".formatted(projectileLauncherType));
        }
    }

    @NotNull
    private <T> T validateSpecVar(@Nullable T value, @NotNull String valueName, @NotNull Object projectileLauncherType) {
        if (value == null) {
            throw new ProjectileLauncherCreationException("Cannot create projectile launcher for type %s because of invalid spec: Required '%s' value is missing".formatted(projectileLauncherType, valueName));
        }

        return value;
    }

    @Nullable
    private ParticleEffect createParticleEffect(@Nullable ParticleEffectSpec spec) {
        ParticleEffect particleEffect = null;

        if (spec != null) {
            particleEffect = particleEffectMapper.map(spec);
        }

        return particleEffect;
    }
}
