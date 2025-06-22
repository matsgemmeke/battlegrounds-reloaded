package nl.matsgemmeke.battlegrounds.item.shoot;

import jakarta.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ShootingSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShootHandlerFactory {

    @NotNull
    private final BulletLauncherFactory bulletLauncherFactory;
    @NotNull
    private final FireModeFactory fireModeFactory;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;

    @Inject
    public ShootHandlerFactory(
            @NotNull BulletLauncherFactory bulletLauncherFactory,
            @NotNull FireModeFactory fireModeFactory,
            @NotNull GameContextProvider contextProvider,
            @NotNull ParticleEffectMapper particleEffectMapper
    ) {
        this.bulletLauncherFactory = bulletLauncherFactory;
        this.fireModeFactory = fireModeFactory;
        this.contextProvider = contextProvider;
        this.particleEffectMapper = particleEffectMapper;
    }

    @NotNull
    public ShootHandler create(@NotNull ShootingSpec spec, @NotNull GameKey gameKey, @NotNull AmmunitionStorage ammunitionStorage, @NotNull ItemRepresentation itemRepresentation) {
        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
        CollisionDetector collisionDetector = contextProvider.getComponent(gameKey, CollisionDetector.class);

        FireMode fireMode = fireModeFactory.create(spec.fireMode());

        List<GameSound> shotSounds = DefaultGameSound.parseSounds(spec.shotSounds());

        ParticleEffect trajectoryParticleEffect = null;
        ParticleEffectSpec trajectoryParticleEffectSpec = spec.projectile().trajectoryParticleEffect();

        if (trajectoryParticleEffectSpec != null) {
            trajectoryParticleEffect = particleEffectMapper.map(trajectoryParticleEffectSpec);
        }

        BulletProperties bulletProperties = new BulletProperties(trajectoryParticleEffect, shotSounds);
        ProjectileLauncher projectileLauncher = bulletLauncherFactory.create(bulletProperties, audioEmitter, collisionDetector);

        ShootHandler shootHandler = new ShootHandler(fireMode, projectileLauncher, ammunitionStorage, itemRepresentation);
        shootHandler.registerObservers();
        return shootHandler;
    }
}
