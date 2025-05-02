package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.OneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemEffectSpecLoader {

    private static final List<String> ALLOWED_TYPE_VALUES = List.of("COMBUSTION", "EXPLOSION", "FLASH", "GUN_FIRE_SIMULATION", "MARK_SPAWN_POINT", "SMOKE_SCREEN", "SOUND_NOTIFICATION");

    private static final String TYPE_ROUTE = "type";
    private static final String RANGE_PROFILE_ROUTE = "range";

    @NotNull
    private final RangeProfileSpecLoader rangeProfileSpecLoader;
    @NotNull
    private final YamlReader yamlReader;

    public ItemEffectSpecLoader(@NotNull YamlReader yamlReader, @NotNull RangeProfileSpecLoader rangeProfileSpecLoader) {
        this.yamlReader = yamlReader;
        this.rangeProfileSpecLoader = rangeProfileSpecLoader;
    }

    @NotNull
    public ItemEffectSpec loadSpec(@NotNull String baseRoute) {
        String typeRoute = this.createRoute(baseRoute, TYPE_ROUTE);
        String rangeProfileRoute = this.createRoute(baseRoute, RANGE_PROFILE_ROUTE);

        String type = new FieldSpecResolver<String>()
                .route(typeRoute)
                .value(yamlReader.getString(typeRoute))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_TYPE_VALUES))
                .resolve();

        RangeProfileSpec rangeProfileSpec = rangeProfileSpecLoader.loadSpec(rangeProfileRoute);

        return new ItemEffectSpec(type, rangeProfileSpec);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
