package nl.matsgemmeke.battlegrounds.configuration.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.validation.OneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredIfFieldEqualsValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpreadPatternSpecLoader {

    private static final List<String> ALLOWED_SPREAD_PATTERN_TYPE_VALUES = List.of("BUCKSHOT", "SINGLE_PROJECTILE");

    private static final String TYPE_ROUTE = "type";
    private static final String PROJECTILE_AMOUNT_ROUTE = "projectile-amount";
    private static final String HORIZONTAL_SPREAD_ROUTE = "horizontal-spread";
    private static final String VERTICAL_SPREAD_ROUTE = "vertical-spread";

    @NotNull
    private final YamlReader yamlReader;

    public SpreadPatternSpecLoader(@NotNull YamlReader yamlReader) {
        this.yamlReader = yamlReader;
    }

    @NotNull
    public SpreadPatternSpec loadSpec(@NotNull String baseRoute) {
        String typeRoute = this.createRoute(baseRoute, TYPE_ROUTE);
        String projectileAmountRoute = this.createRoute(baseRoute, PROJECTILE_AMOUNT_ROUTE);
        String horizontalSpreadRoute = this.createRoute(baseRoute, HORIZONTAL_SPREAD_ROUTE);
        String verticalSpreadRoute = this.createRoute(baseRoute, VERTICAL_SPREAD_ROUTE);

        String type = new FieldSpecResolver<String>()
                .route(typeRoute)
                .value(yamlReader.getString(typeRoute))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_SPREAD_PATTERN_TYPE_VALUES))
                .resolve();
        Integer projectileAmount = new FieldSpecResolver<Integer>()
                .route(projectileAmountRoute)
                .value(yamlReader.getOptionalInt(projectileAmountRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "BUCKSHOT"))
                .resolve();
        Float horizontalSpread = new FieldSpecResolver<Float>()
                .route(horizontalSpreadRoute)
                .value(yamlReader.getOptionalFloat(horizontalSpreadRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "BUCKSHOT"))
                .resolve();
        Float verticalSpread = new FieldSpecResolver<Float>()
                .route(verticalSpreadRoute)
                .value(yamlReader.getOptionalFloat(verticalSpreadRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "BUCKSHOT"))
                .resolve();

        return new SpreadPatternSpec(type, projectileAmount, horizontalSpread, verticalSpread);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
