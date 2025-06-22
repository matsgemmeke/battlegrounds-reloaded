package nl.matsgemmeke.battlegrounds.item.shoot;

import jakarta.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ShootingSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletProperties;
import org.jetbrains.annotations.NotNull;

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
    public ShootHandler create(@NotNull ShootingSpec spec, @NotNull GameKey gameKey, @NotNull ItemRepresentation itemRepresentation) {
        CollisionDetector collisionDetector = contextProvider.getComponent(gameKey, CollisionDetector.class);

        FireMode fireMode = fireModeFactory.create(spec.fireMode());

        ParticleEffect trajectoryParticleEffect = null;
        ParticleEffectSpec trajectoryParticleEffectSpec = spec.projectile().trajectoryParticleEffect();

        if (trajectoryParticleEffectSpec != null) {
            trajectoryParticleEffect = particleEffectMapper.map(trajectoryParticleEffectSpec);
        }

        BulletProperties bulletProperties = new BulletProperties(trajectoryParticleEffect);
        ProjectileLauncher projectileLauncher = bulletLauncherFactory.create(bulletProperties, collisionDetector);

        ShootHandler shootHandler = new ShootHandler(fireMode, projectileLauncher, itemRepresentation);
        shootHandler.registerObservers();
        return shootHandler;
    }
}
