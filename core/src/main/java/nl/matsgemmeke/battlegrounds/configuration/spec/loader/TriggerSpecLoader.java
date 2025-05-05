package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.OneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredIfFieldEqualsValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class TriggerSpecLoader {

    private static final List<String> ALLOWED_TYPE_VALUES = List.of("ACTIVATOR", "ENEMY_PROXIMITY", "FLOOR_HIT", "TIMED");
    private static final String TYPE_ROUTE = "type";
    private static final String CHECK_RANGE_ROUTE = "check-range";
    private static final String CHECK_INTERVAL_ROUTE = "check-interval";
    private static final String DELAY_UNTIL_ACTIVATION_ROUTE = "delay-until-activation";

    @NotNull
    private final YamlReader yamlReader;

    public TriggerSpecLoader(@NotNull YamlReader yamlReader) {
        this.yamlReader = yamlReader;
    }

    @NotNull
    public TriggerSpec loadSpec(@NotNull String baseRoute) {
        String typeRoute = this.createRoute(baseRoute, TYPE_ROUTE);
        String checkRangeRoute = this.createRoute(baseRoute, CHECK_RANGE_ROUTE);
        String checkIntervalRoute = this.createRoute(baseRoute, CHECK_INTERVAL_ROUTE);
        String delayUntilActivationRoute = this.createRoute(baseRoute, DELAY_UNTIL_ACTIVATION_ROUTE);

        String type = new FieldSpecResolver<String>()
                .route(typeRoute)
                .value(yamlReader.getString(typeRoute))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_TYPE_VALUES))
                .resolve();
        Double checkRange = new FieldSpecResolver<Double>()
                .route(checkRangeRoute)
                .value(yamlReader.getOptionalDouble(checkRangeRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "ENEMY_PROXIMITY"))
                .resolve();
        Long checkInterval = new FieldSpecResolver<Long>()
                .route(checkIntervalRoute)
                .value(yamlReader.getOptionalLong(checkIntervalRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("ENEMY_PROMIXITY", "FLOOR_HIT")))
                .resolve();
        Long delayUntilActivation = new FieldSpecResolver<Long>()
                .route(delayUntilActivationRoute)
                .value(yamlReader.getOptionalLong(delayUntilActivationRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "TIMED"))
                .resolve();

        return new TriggerSpec(type, checkRange, checkInterval, delayUntilActivation);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
