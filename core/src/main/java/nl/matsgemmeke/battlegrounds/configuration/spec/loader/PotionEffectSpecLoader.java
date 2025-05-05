package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.PotionEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.OneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PotionEffectSpecLoader {

    private static final Set<String> ALLOWED_POTION_EFFECT_TYPES = Set.of("SPEED", "SLOW", "FAST_DIGGING", "SLOW_DIGGING", "INCREASE_DAMAGE", "HEAL", "HARM", "JUMP", "CONFUSION", "REGENERATION", "DAMAGE_RESISTANCE", "FIRE_RESISTANCE", "WATER_BREATHING", "INVISIBILITY", "BLINDNESS", "NIGHT_VISION", "HUNGER", "WEAKNESS", "POISON", "WITHER", "HEALTH_BOOST", "ABSORPTION", "SATURATION", "GLOWING", "LEVITATION", "LUCK", "UNLUCK", "SLOW_FALLING", "CONDUIT_POWER", "DOLPHINS_GRACE", "BAD_OMEN", "HERO_OF_THE_VILLAGE", "DARKNESS");

    private static final String TYPE_ROUTE = "type";
    private static final String DURATION_ROUTE = "duration";
    private static final String AMPLIFIER_ROUTE = "amplifier";
    private static final String AMBIENT_ROUTE = "ambient";
    private static final String PARTICLES_ROUTE = "particles";
    private static final String ICON_ROUTE = "icon";

    @NotNull
    private final YamlReader yamlReader;

    public PotionEffectSpecLoader(@NotNull YamlReader yamlReader) {
        this.yamlReader = yamlReader;
    }

    @NotNull
    public PotionEffectSpec loadSpec(@NotNull String baseRoute) {
        String typeRoute = this.createRoute(baseRoute, TYPE_ROUTE);
        String durationRoute = this.createRoute(baseRoute, DURATION_ROUTE);
        String amplifierRoute = this.createRoute(baseRoute, AMPLIFIER_ROUTE);
        String ambientRoute = this.createRoute(baseRoute, AMBIENT_ROUTE);
        String particlesRoute = this.createRoute(baseRoute, PARTICLES_ROUTE);
        String iconRoute = this.createRoute(baseRoute, ICON_ROUTE);

        String type = new FieldSpecResolver<String>()
                .route(typeRoute)
                .value(yamlReader.getString(typeRoute))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_POTION_EFFECT_TYPES))
                .resolve();
        Integer duration = new FieldSpecResolver<Integer>()
                .route(durationRoute)
                .value(yamlReader.getOptionalInt(durationRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Integer amplifier = new FieldSpecResolver<Integer>()
                .route(amplifierRoute)
                .value(yamlReader.getOptionalInt(amplifierRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Boolean ambient = new FieldSpecResolver<Boolean>()
                .route(ambientRoute)
                .value(yamlReader.getOptionalBoolean(ambientRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Boolean particles = new FieldSpecResolver<Boolean>()
                .route(particlesRoute)
                .value(yamlReader.getOptionalBoolean(particlesRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Boolean icon = new FieldSpecResolver<Boolean>()
                .route(iconRoute)
                .value(yamlReader.getOptionalBoolean(iconRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();

        return new PotionEffectSpec(type, duration, amplifier, ambient, particles, icon);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
