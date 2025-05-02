package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
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

    @NotNull
    private final RangeProfileSpecLoader rangeProfileSpecLoader;
    @NotNull
    private final TriggerSpecLoader triggerSpecLoader;
    @NotNull
    private final YamlReader yamlReader;

    public ItemEffectSpecLoader(@NotNull YamlReader yamlReader, @NotNull RangeProfileSpecLoader rangeProfileSpecLoader, @NotNull TriggerSpecLoader triggerSpecLoader) {
        this.yamlReader = yamlReader;
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

        RangeProfileSpec rangeProfileSpec = rangeProfileSpecLoader.loadSpec(rangeProfileRoute);

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

        return new ItemEffectSpec(type, triggerSpecs, rangeProfileSpec, maxSize, minSize, density, growth, growthInterval);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
