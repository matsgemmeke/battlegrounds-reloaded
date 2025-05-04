package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ActivationPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.OneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredIfFieldEqualsValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemEffectSpecLoader {

    private static final List<String> ALLOWED_TYPE_VALUES = List.of("COMBUSTION", "EXPLOSION", "FLASH", "GUN_FIRE_SIMULATION", "MARK_SPAWN_POINT", "SMOKE_SCREEN", "SOUND_NOTIFICATION");

    private static final String TYPE_ROUTE = "type";
    private static final String TRIGGERS_ROUTE = "triggers";
    private static final String RANGE_PROFILE_ROUTE = "range";

    private static final String MAX_SIZE_ROUTE = "max-size";
    private static final String MIN_SIZE_ROUTE = "min-size";
    private static final String DENSITY_ROUTE = "density";
    private static final String GROWTH_ROUTE = "growth";
    private static final String GROWTH_INTERVAL_ROUTE = "growth-interval";
    private static final String MAX_DURATION_ROUTE = "max-duration";
    private static final String MIN_DURATION_ROUTE = "min-duration";
    private static final String ACTIVATION_SOUNDS_ROUTE = "activation-sounds";

    private static final String POWER_ROUTE = "power";
    private static final String DAMAGE_BLOCKS_ROUTE = "damage-blocks";
    private static final String SPREAD_FIRE_ROUTE = "spread-fire";

    private static final String PARTICLE_EFFECT_ROUTE = "particle-effect";
    private static final String ACTIVATION_PATTERN_ROUTE = "activation-pattern";

    @NotNull
    private final ActivationPatternSpecLoader activationPatternSpecLoader;
    @NotNull
    private final ParticleEffectSpecLoader particleEffectSpecLoader;
    @NotNull
    private final RangeProfileSpecLoader rangeProfileSpecLoader;
    @NotNull
    private final TriggerSpecLoader triggerSpecLoader;
    @NotNull
    private final YamlReader yamlReader;

    public ItemEffectSpecLoader(
            @NotNull YamlReader yamlReader,
            @NotNull ActivationPatternSpecLoader activationPatternSpecLoader,
            @NotNull ParticleEffectSpecLoader particleEffectSpecLoader,
            @NotNull RangeProfileSpecLoader rangeProfileSpecLoader,
            @NotNull TriggerSpecLoader triggerSpecLoader
    ) {
        this.yamlReader = yamlReader;
        this.activationPatternSpecLoader = activationPatternSpecLoader;
        this.particleEffectSpecLoader = particleEffectSpecLoader;
        this.rangeProfileSpecLoader = rangeProfileSpecLoader;
        this.triggerSpecLoader = triggerSpecLoader;
    }

    @NotNull
    public ItemEffectSpec loadSpec(@NotNull String baseRoute) {
        String typeRoute = this.createRoute(baseRoute, TYPE_ROUTE);
        String triggersRoute = this.createRoute(baseRoute, TRIGGERS_ROUTE);
        String rangeProfileRoute = this.createRoute(baseRoute, RANGE_PROFILE_ROUTE);

        String maxSizeRoute = this.createRoute(baseRoute, MAX_SIZE_ROUTE);
        String minSizeRoute = this.createRoute(baseRoute, MIN_SIZE_ROUTE);
        String densityRoute = this.createRoute(baseRoute, DENSITY_ROUTE);
        String growthRoute = this.createRoute(baseRoute, GROWTH_ROUTE);
        String growthIntervalRoute = this.createRoute(baseRoute, GROWTH_INTERVAL_ROUTE);
        String maxDurationRoute = this.createRoute(baseRoute, MAX_DURATION_ROUTE);
        String minDurationRoute = this.createRoute(baseRoute, MIN_DURATION_ROUTE);
        String activationSoundsRoute = this.createRoute(baseRoute, ACTIVATION_SOUNDS_ROUTE);

        String powerRoute = this.createRoute(baseRoute, POWER_ROUTE);
        String damageBlockRoute = this.createRoute(baseRoute, DAMAGE_BLOCKS_ROUTE);
        String spreadFireRoute = this.createRoute(baseRoute, SPREAD_FIRE_ROUTE);

        String particleEffectRoute = this.createRoute(baseRoute, PARTICLE_EFFECT_ROUTE);
        String activationPatternRoute = this.createRoute(baseRoute, ACTIVATION_PATTERN_ROUTE);

        String type = new FieldSpecResolver<String>()
                .route(typeRoute)
                .value(yamlReader.getString(typeRoute))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_TYPE_VALUES))
                .resolve();

        List<TriggerSpec> triggerSpecs = new ArrayList<>();

        for (String key : yamlReader.getRoutes(triggersRoute)) {
            String triggerRoute = this.createRoute(baseRoute, TRIGGERS_ROUTE + "." + key);
            TriggerSpec triggerSpec = triggerSpecLoader.loadSpec(triggerRoute);

            triggerSpecs.add(triggerSpec);
        }

        RangeProfileSpec rangeProfileSpec = null;

        if (yamlReader.contains(rangeProfileRoute)) {
            rangeProfileSpec = rangeProfileSpecLoader.loadSpec(rangeProfileRoute);
        }

        Double maxSize = new FieldSpecResolver<Double>()
                .route(maxSizeRoute)
                .value(yamlReader.getOptionalDouble(maxSizeRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("COMBUSTION", "SMOKE_SCREEN")))
                .resolve();
        Double minSize = new FieldSpecResolver<Double>()
                .route(minSizeRoute)
                .value(yamlReader.getOptionalDouble(minSizeRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "SMOKE_SCREEN"))
                .resolve();
        Double density = new FieldSpecResolver<Double>()
                .route(densityRoute)
                .value(yamlReader.getOptionalDouble(densityRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "SMOKE_SCREEN"))
                .resolve();
        Double growth = new FieldSpecResolver<Double>()
                .route(growthRoute)
                .value(yamlReader.getOptionalDouble(growthRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("COMBUSTION", "SMOKE_SCREEN")))
                .resolve();
        Long growthInterval = new FieldSpecResolver<Long>()
                .route(growthIntervalRoute)
                .value(yamlReader.getOptionalLong(growthIntervalRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("COMBUSTION", "SMOKE_SCREEN")))
                .resolve();
        Long maxDuration = new FieldSpecResolver<Long>()
                .route(maxDurationRoute)
                .value(yamlReader.getOptionalLong(maxDurationRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("COMBUSITON", "GUN_FIRE_SIMULATION", "SMOKE_SCREEN")))
                .resolve();
        Long minDuration = new FieldSpecResolver<Long>()
                .route(minDurationRoute)
                .value(yamlReader.getOptionalLong(minDurationRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("COMBUSITON", "GUN_FIRE_SIMULATION", "SMOKE_SCREEN")))
                .resolve();
        String activationSounds = new FieldSpecResolver<String>()
                .route(activationSoundsRoute)
                .value(yamlReader.getString(activationSoundsRoute))
                .resolve();

        Float power = new FieldSpecResolver<Float>()
                .route(powerRoute)
                .value(yamlReader.getOptionalFloat(powerRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("EXPLOSION", "FLASH")))
                .resolve();
        Boolean damageBlocks = new FieldSpecResolver<Boolean>()
                .route(damageBlockRoute)
                .value(yamlReader.getOptionalBoolean(damageBlockRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("COMBUSTION", "EXPLOSION", "FLASH")))
                .resolve();
        Boolean spreadFire = new FieldSpecResolver<Boolean>()
                .route(spreadFireRoute)
                .value(yamlReader.getOptionalBoolean(spreadFireRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("COMBUSTION", "EXPLOSION", "FLASH")))
                .resolve();

        ParticleEffectSpec particleEffectSpec = null;
        ActivationPatternSpec activationPatternSpec = null;

        if (yamlReader.contains(particleEffectRoute)) {
            particleEffectSpec = particleEffectSpecLoader.loadSpec(particleEffectRoute);
        }

        if (yamlReader.contains(activationPatternRoute)) {
            activationPatternSpec = activationPatternSpecLoader.loadSpec(activationPatternRoute);
        }

        return new ItemEffectSpec(type, triggerSpecs, rangeProfileSpec, maxSize, minSize, density, growth, growthInterval, maxDuration, minDuration, activationSounds, power, damageBlocks, spreadFire, particleEffectSpec, activationPatternSpec);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
