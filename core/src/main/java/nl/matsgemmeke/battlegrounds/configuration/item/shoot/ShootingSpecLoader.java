package nl.matsgemmeke.battlegrounds.configuration.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpecLoader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.validation.OneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredIfFieldEqualsValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ShootingSpecLoader {

    private static final List<String> ALLOWED_FIRE_MODE_TYPE_VALUES = List.of("BURST_MODE", "FULLY_AUTOMATIC", "SEMI_AUTOMATIC");
    private static final List<String> ALLOWED_RECOIL_TYPE_VALUES = List.of("CAMERA_MOVEMENT", "RANDOM_SPREAD");

    private static final String SHOT_SOUNDS_ROUTE = "shot-sounds";

    private static final String FIRE_MODE_TYPE_ROUTE = "fire-mode.type";
    private static final String FIRE_MODE_AMOUNT_OF_SHOTS_ROUTE = "fire-mode.amount-of-shots";
    private static final String FIRE_MODE_RATE_OF_FIRE_ROUTE = "fire-mode.rate-of-fire";
    private static final String FIRE_MODE_CYCLE_COOLDOWN_ROUTE = "fire-mode.cycle-cooldown";

    private static final String PROJECTILE_TRAJECTORY_PARTICLE_EFFECT_ROUTE = "projectile.trajectory-particle-effect";

    private static final String RECOIL_TYPE_ROUTE = "recoil.type";
    private static final String RECOIL_HORIZONTAL_ROUTE = "recoil.horizontal";
    private static final String RECOIL_VERTICAL_ROUTE = "recoil.vertical";
    private static final String RECOIL_KICKBACK_DURATION_ROUTE = "recoil.kickback-duration";
    private static final String RECOIL_RECOVERY_RATE_ROUTE = "recoil.recovery-rate";
    private static final String RECOIL_RECOVERY_DURATION_ROUTE = "recoil.recovery-duration";

    private static final String SPREAD_PATTERN_ROUTE = "spread-pattern";

    @NotNull
    private final ParticleEffectSpecLoader particleEffectSpecLoader;
    @NotNull
    private final SpreadPatternSpecLoader spreadPatternSpecLoader;
    @NotNull
    private final YamlReader yamlReader;

    public ShootingSpecLoader(
            @NotNull YamlReader yamlReader,
            @NotNull ParticleEffectSpecLoader particleEffectSpecLoader,
            @NotNull SpreadPatternSpecLoader spreadPatternSpecLoader
    ) {
        this.yamlReader = yamlReader;
        this.particleEffectSpecLoader = particleEffectSpecLoader;
        this.spreadPatternSpecLoader = spreadPatternSpecLoader;
    }

    @NotNull
    public ShootingSpec loadSpec(@NotNull String baseRoute) {
        String shotSoundsRoute = this.createRoute(baseRoute, SHOT_SOUNDS_ROUTE);

        String fireModeTypeRoute = this.createRoute(baseRoute, FIRE_MODE_TYPE_ROUTE);
        String fireModeAmountOfShotsRoute = this.createRoute(baseRoute, FIRE_MODE_AMOUNT_OF_SHOTS_ROUTE);
        String fireModeRateOfFireRoute = this.createRoute(baseRoute, FIRE_MODE_RATE_OF_FIRE_ROUTE);
        String fireModeCycleCooldownRoute = this.createRoute(baseRoute, FIRE_MODE_CYCLE_COOLDOWN_ROUTE);

        String projectileTrajectoryParticleEffectRoute = this.createRoute(baseRoute, PROJECTILE_TRAJECTORY_PARTICLE_EFFECT_ROUTE);

        String recoilTypeRoute = this.createRoute(baseRoute, RECOIL_TYPE_ROUTE);
        String recoilHorizontalRoute = this.createRoute(baseRoute, RECOIL_HORIZONTAL_ROUTE);
        String recoilVerticalRoute = this.createRoute(baseRoute, RECOIL_VERTICAL_ROUTE);
        String recoilKickbackDurationRoute = this.createRoute(baseRoute, RECOIL_KICKBACK_DURATION_ROUTE);
        String recoilRecoveryRateRoute = this.createRoute(baseRoute, RECOIL_RECOVERY_RATE_ROUTE);
        String recoilRecoveryDurationRoute = this.createRoute(baseRoute, RECOIL_RECOVERY_DURATION_ROUTE);

        String spreadPatternRoute = this.createRoute(baseRoute, SPREAD_PATTERN_ROUTE);

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

        String recoilType = new FieldSpecResolver<String>()
                .route(recoilTypeRoute)
                .value(yamlReader.getString(recoilTypeRoute))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_RECOIL_TYPE_VALUES))
                .resolve();
        List<Float> horizontalRecoilValues = new FieldSpecResolver<List<Float>>()
                .route(recoilHorizontalRoute)
                .value(yamlReader.getOptionalFloatList(recoilHorizontalRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        List<Float> verticalRecoilValues = new FieldSpecResolver<List<Float>>()
                .route(recoilVerticalRoute)
                .value(yamlReader.getOptionalFloatList(recoilVerticalRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Long kickbackDuration = new FieldSpecResolver<Long>()
                .route(recoilKickbackDurationRoute)
                .value(yamlReader.getOptionalLong(recoilKickbackDurationRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(recoilTypeRoute, recoilType, "CAMERA_MOVEMENT"))
                .resolve();
        Float recoveryRate = new FieldSpecResolver<Float>()
                .route(recoilRecoveryRateRoute)
                .value(yamlReader.getOptionalFloat(recoilRecoveryRateRoute).orElse(0.0f))
                .resolve();
        Long recoveryDuration = new FieldSpecResolver<Long>()
                .route(recoilRecoveryDurationRoute)
                .value(yamlReader.getOptionalLong(recoilRecoveryDurationRoute).orElse(0L))
                .resolve();
        RecoilSpec recoilSpec = new RecoilSpec(recoilType, horizontalRecoilValues, verticalRecoilValues, kickbackDuration, recoveryRate, recoveryDuration);

        SpreadPatternSpec spreadPatternSpec = spreadPatternSpecLoader.loadSpec(spreadPatternRoute);

        return new ShootingSpec(fireModeSpec, projectileSpec, recoilSpec, spreadPatternSpec, shotSounds);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
