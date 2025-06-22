package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ShootingSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.OneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredIfFieldEqualsValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ShootingSpecLoader {

    private static final List<String> ALLOWED_FIRE_MODE_TYPE_VALUES = List.of("BURST_MODE", "FULLY_AUTOMATIC", "SEMI_AUTOMATIC");

    private static final String SHOT_SOUNDS_ROUTE = "shot-sounds";

    private static final String FIRE_MODE_TYPE_ROUTE = "fire-mode.type";
    private static final String FIRE_MODE_AMOUNT_OF_SHOTS_ROUTE = "fire-mode.amount-of-shots";
    private static final String FIRE_MODE_RATE_OF_FIRE_ROUTE = "fire-mode.rate-of-fire";
    private static final String FIRE_MODE_CYCLE_COOLDOWN_ROUTE = "fire-mode.cycle-cooldown";

    private static final String PROJECTILE_TRAJECTORY_PARTICLE_EFFECT_ROUTE = "projectile.trajectory-particle-effect";

    @NotNull
    private final ParticleEffectSpecLoader particleEffectSpecLoader;
    @NotNull
    private final YamlReader yamlReader;

    public ShootingSpecLoader(@NotNull YamlReader yamlReader, @NotNull ParticleEffectSpecLoader particleEffectSpecLoader) {
        this.yamlReader = yamlReader;
        this.particleEffectSpecLoader = particleEffectSpecLoader;
    }

    @NotNull
    public ShootingSpec loadSpec(@NotNull String baseRoute) {
        String shotSoundsRoute = this.createRoute(baseRoute, SHOT_SOUNDS_ROUTE);
        String fireModeTypeRoute = this.createRoute(baseRoute, FIRE_MODE_TYPE_ROUTE);
        String fireModeAmountOfShotsRoute = this.createRoute(baseRoute, FIRE_MODE_AMOUNT_OF_SHOTS_ROUTE);
        String fireModeRateOfFireRoute = this.createRoute(baseRoute, FIRE_MODE_RATE_OF_FIRE_ROUTE);
        String fireModeCycleCooldownRoute = this.createRoute(baseRoute, FIRE_MODE_CYCLE_COOLDOWN_ROUTE);
        String projectileTrajectoryParticleEffectRoute = this.createRoute(baseRoute, PROJECTILE_TRAJECTORY_PARTICLE_EFFECT_ROUTE);

        String shotSounds = new FieldSpecResolver<String>()
                .route(shotSoundsRoute)
                .value(yamlReader.getString(shotSoundsRoute))
                .resolve();

        String fireModeType = new FieldSpecResolver<String>()
                .route(fireModeTypeRoute)
                .value(yamlReader.getString(fireModeTypeRoute))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_FIRE_MODE_TYPE_VALUES))
                .resolve();
        Integer amountOfShots = new FieldSpecResolver<Integer>()
                .route(fireModeAmountOfShotsRoute)
                .value(yamlReader.getOptionalInt(fireModeAmountOfShotsRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(fireModeTypeRoute, fireModeType, "BURST_MODE"))
                .resolve();
        Integer rateOfFire = new FieldSpecResolver<Integer>()
                .route(fireModeRateOfFireRoute)
                .value(yamlReader.getOptionalInt(fireModeRateOfFireRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(fireModeTypeRoute, fireModeType, Set.of("BURST_MODE", "FULLY_AUTOMATIC")))
                .resolve();
        Long delayBetweenShots = new FieldSpecResolver<Long>()
                .route(fireModeCycleCooldownRoute)
                .value(yamlReader.getOptionalLong(fireModeCycleCooldownRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(fireModeTypeRoute, fireModeType, Set.of("BURST_MODE", "SEMI_AUTOMATIC")))
                .resolve();
        FireModeSpec fireModeSpec = new FireModeSpec(fireModeType, amountOfShots, rateOfFire, delayBetweenShots);

        ParticleEffectSpec projectileTrajectoryParticleEffectSpec = particleEffectSpecLoader.loadSpec(projectileTrajectoryParticleEffectRoute);
        ProjectileSpec projectileSpec = new ProjectileSpec(projectileTrajectoryParticleEffectSpec);

        return new ShootingSpec(fireModeSpec, projectileSpec, shotSounds);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
