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
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProjectileLauncherFactory {

    @NotNull
    private final FireballLauncherFactory fireballLauncherFactory;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final HitscanLauncherFactory hitscanLauncherFactory;
    @NotNull
    private final ItemEffectFactory itemEffectFactory;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;

    @Inject
    public ProjectileLauncherFactory(
            @NotNull FireballLauncherFactory fireballLauncherFactory,
            @NotNull GameContextProvider contextProvider,
            @NotNull HitscanLauncherFactory hitscanLauncherFactory,
            @NotNull ItemEffectFactory itemEffectFactory,
            @NotNull ParticleEffectMapper particleEffectMapper
    ) {
        this.fireballLauncherFactory = fireballLauncherFactory;
        this.contextProvider = contextProvider;
        this.hitscanLauncherFactory = hitscanLauncherFactory;
        this.itemEffectFactory = itemEffectFactory;
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
                ItemEffect itemEffect = itemEffectFactory.create(spec.effect, gameKey);

                ProjectileHitActionRegistry projectileHitActionRegistry = contextProvider.getComponent(gameKey, ProjectileHitActionRegistry.class);

                return fireballLauncherFactory.create(properties, audioEmitter, itemEffect, projectileHitActionRegistry);
            }
            case HITSCAN -> {
                List<GameSound> shotSounds = DefaultGameSound.parseSounds(spec.shotSounds);
                ParticleEffect trajectoryParticleEffect = null;
                ParticleEffectSpec trajectoryParticleEffectSpec = spec.trajectoryParticleEffect;

                if (trajectoryParticleEffectSpec != null) {
                    trajectoryParticleEffect = particleEffectMapper.map(trajectoryParticleEffectSpec);
                }

                HitscanProperties properties = new HitscanProperties(shotSounds, trajectoryParticleEffect);
                ItemEffect itemEffect = itemEffectFactory.create(spec.effect, gameKey);

                return hitscanLauncherFactory.create(properties, audioEmitter, collisionDetector, itemEffect, targetFinder);
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
