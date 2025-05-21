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

    private static final List<String> ALLOWED_TYPE_VALUES = List.of("ENEMY_PROXIMITY", "FLOOR_HIT", "IMPACT", "SEQUENCE", "TIMED");
    private static final String TYPE_ROUTE = "type";
    private static final String DELAY_ROUTE = "delay";
    private static final String INTERVAL_ROUTE = "interval";
    private static final String OFFSET_DELAYS_ROUTE = "offset-delays";
    private static final String RANGE_ROUTE = "range";

    @NotNull
    private final YamlReader yamlReader;

    public TriggerSpecLoader(@NotNull YamlReader yamlReader) {
        this.yamlReader = yamlReader;
    }

    @NotNull
    public TriggerSpec loadSpec(@NotNull String baseRoute) {
        String typeRoute = this.createRoute(baseRoute, TYPE_ROUTE);
        String delayRoute = this.createRoute(baseRoute, DELAY_ROUTE);
        String intervalRoute = this.createRoute(baseRoute, INTERVAL_ROUTE);
        String offsetDelaysRoute = this.createRoute(baseRoute, OFFSET_DELAYS_ROUTE);
        String rangeRoute = this.createRoute(baseRoute, RANGE_ROUTE);

        String type = new FieldSpecResolver<String>()
                .route(typeRoute)
                .value(yamlReader.getString(typeRoute))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_TYPE_VALUES))
                .resolve();
        Long delay = new FieldSpecResolver<Long>()
                .route(delayRoute)
                .value(yamlReader.getOptionalLong(delayRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("ENEMY_PROXIMITY", "FLOOR_HIT", "IMPACT", "TIMED")))
                .resolve();
        Long interval = new FieldSpecResolver<Long>()
                .route(intervalRoute)
                .value(yamlReader.getOptionalLong(intervalRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("ENEMY_PROXIMITY", "FLOOR_HIT", "IMPACT")))
                .resolve();
        List<Long> offsetDelays = new FieldSpecResolver<List<Long>>()
                .route(offsetDelaysRoute)
                .value(yamlReader.getOptionalLongList(offsetDelaysRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "SEQUENCE"))
                .resolve();
        Double range = new FieldSpecResolver<Double>()
                .route(rangeRoute)
                .value(yamlReader.getOptionalDouble(rangeRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "ENEMY_PROXIMITY"))
                .resolve();

        return new TriggerSpec(type, delay, interval, offsetDelays, range);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
