package nl.matsgemmeke.battlegrounds.configuration.item.particle;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.validation.RegexValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

public class DustOptionsSpecLoader {

    private static final String COLOR_ROUTE = "color";
    private static final String COLOR_REGEX = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";
    private static final String SIZE_ROUTE = "size";

    @NotNull
    private final YamlReader yamlReader;

    public DustOptionsSpecLoader(@NotNull YamlReader yamlReader) {
        this.yamlReader = yamlReader;
    }

    @NotNull
    public DustOptionsSpec loadSpec(@NotNull String baseRoute) {
        String colorRoute = this.createRoute(baseRoute, COLOR_ROUTE);
        String sizeRoute = this.createRoute(baseRoute, SIZE_ROUTE);

        String color = new FieldSpecResolver<String>()
                .route(colorRoute)
                .value(yamlReader.getString(colorRoute))
                .validate(new RequiredValidator<>())
                .validate(new RegexValidator(COLOR_REGEX))
                .resolve();
        Integer size = new FieldSpecResolver<Integer>()
                .route(sizeRoute)
                .value(yamlReader.getOptionalInt(sizeRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();

        return new DustOptionsSpec(color, size);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
