package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

public class RangeProfileSpecLoader {

    private static final String SHORT_RANGE_DAMAGE_ROUTE = "short-range.damage";
    private static final String SHORT_RANGE_DISTANCE_ROUTE = "short-range.distance";
    private static final String MEDIUM_RANGE_DAMAGE_ROUTE = "medium-range.damage";
    private static final String MEDIUM_RANGE_DISTANCE_ROUTE = "medium-range.distance";
    private static final String LONG_RANGE_DAMAGE_ROUTE = "long-range.damage";
    private static final String LONG_RANGE_DISTANCE_ROUTE = "long-range.distance";

    @NotNull
    private final YamlReader yamlReader;

    public RangeProfileSpecLoader(@NotNull YamlReader yamlReader) {
        this.yamlReader = yamlReader;
    }

    @NotNull
    public RangeProfileSpec loadSpec(@NotNull String baseRoute) {
        String shortRangeDamageRoute = this.createRoute(baseRoute, SHORT_RANGE_DAMAGE_ROUTE);
        String shortRangeDistanceRoute = this.createRoute(baseRoute, SHORT_RANGE_DISTANCE_ROUTE);
        String mediumRangeDamageRoute = this.createRoute(baseRoute, MEDIUM_RANGE_DAMAGE_ROUTE);
        String mediumRangeDistanceRoute = this.createRoute(baseRoute, MEDIUM_RANGE_DISTANCE_ROUTE);
        String longRangeDamageRoute = this.createRoute(baseRoute, LONG_RANGE_DAMAGE_ROUTE);
        String longRangeDistanceRoute = this.createRoute(baseRoute, LONG_RANGE_DISTANCE_ROUTE);

        Double shortRangeDamage = new FieldSpecResolver<Double>()
                .route(shortRangeDamageRoute)
                .value(yamlReader.getOptionalDouble(shortRangeDamageRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Double shortRangeDistance = new FieldSpecResolver<Double>()
                .route(shortRangeDistanceRoute)
                .value(yamlReader.getOptionalDouble(shortRangeDistanceRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Double mediumRangeDamage = new FieldSpecResolver<Double>()
                .route(mediumRangeDamageRoute)
                .value(yamlReader.getOptionalDouble(mediumRangeDamageRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Double mediumRangeDistance = new FieldSpecResolver<Double>()
                .route(mediumRangeDistanceRoute)
                .value(yamlReader.getOptionalDouble(mediumRangeDistanceRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Double longRangeDamage = new FieldSpecResolver<Double>()
                .route(longRangeDamageRoute)
                .value(yamlReader.getOptionalDouble(longRangeDamageRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Double longRangeDistance = new FieldSpecResolver<Double>()
                .route(longRangeDistanceRoute)
                .value(yamlReader.getOptionalDouble(longRangeDistanceRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();

        return new RangeProfileSpec(shortRangeDamage, shortRangeDistance, mediumRangeDamage, mediumRangeDistance, longRangeDamage, longRangeDistance);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
