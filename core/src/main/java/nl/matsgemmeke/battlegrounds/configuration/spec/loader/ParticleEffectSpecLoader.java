package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.DustOptionsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class ParticleEffectSpecLoader {

    private static final String PARTICLE_ROUTE = "particle";
    private static final String COUNT_ROUTE = "count";
    private static final String OFFSET_X_ROUTE = "offset-x";
    private static final String OFFSET_Y_ROUTE = "offset-y";
    private static final String OFFSET_Z_ROUTE = "offset-z";
    private static final String EXTRA_ROUTE = "extra";
    private static final String BLOCK_DATA_ROUTE = "block-data";
    private static final String DUST_OPTIONS_ROUTE = "dust-options";

    @NotNull
    private final DustOptionsSpecLoader dustOptionsSpecLoader;
    @NotNull
    private final YamlReader yamlReader;

    public ParticleEffectSpecLoader(@NotNull YamlReader yamlReader, @NotNull DustOptionsSpecLoader dustOptionsSpecLoader) {
        this.yamlReader = yamlReader;
        this.dustOptionsSpecLoader = dustOptionsSpecLoader;
    }

    @NotNull
    public ParticleEffectSpec loadSpec(@NotNull String baseRoute) {
        String particleRoute = this.createRoute(baseRoute, PARTICLE_ROUTE);
        String countRoute = this.createRoute(baseRoute, COUNT_ROUTE);
        String offsetXRoute = this.createRoute(baseRoute, OFFSET_X_ROUTE);
        String offsetYRoute = this.createRoute(baseRoute, OFFSET_Y_ROUTE);
        String offsetZRoute = this.createRoute(baseRoute, OFFSET_Z_ROUTE);
        String extraRoute = this.createRoute(baseRoute, EXTRA_ROUTE);
        String blockDataRoute = this.createRoute(baseRoute, BLOCK_DATA_ROUTE);
        String dustOptionsRoute = this.createRoute(baseRoute, DUST_OPTIONS_ROUTE);

        String particle = new FieldSpecResolver<String>()
                .route(particleRoute)
                .value(yamlReader.getString(particleRoute))
                .validate(new RequiredValidator<>())
                .validate(new EnumValidator<>(Particle.class))
                .resolve();
        Integer count = new FieldSpecResolver<Integer>()
                .route(countRoute)
                .value(yamlReader.getOptionalInt(countRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Double offsetX = new FieldSpecResolver<Double>()
                .route(offsetXRoute)
                .value(yamlReader.getOptionalDouble(offsetXRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Double offsetY = new FieldSpecResolver<Double>()
                .route(offsetYRoute)
                .value(yamlReader.getOptionalDouble(offsetYRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Double offsetZ = new FieldSpecResolver<Double>()
                .route(offsetZRoute)
                .value(yamlReader.getOptionalDouble(offsetZRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Double extra = new FieldSpecResolver<Double>()
                .route(extraRoute)
                .value(yamlReader.getOptionalDouble(extraRoute).orElse(null))
                .resolve();
        String blockData = new FieldSpecResolver<String>()
                .route(blockDataRoute)
                .value(yamlReader.getString(blockDataRoute))
                .validate(new EnumValidator<>(Material.class))
                .resolve();

        DustOptionsSpec dustOptionsSpec = null;

        if (yamlReader.contains(dustOptionsRoute)) {
            dustOptionsSpec = dustOptionsSpecLoader.loadSpec(dustOptionsRoute);
        }

        return new ParticleEffectSpec(particle, count, offsetX, offsetY, offsetZ, extra, blockData, dustOptionsSpec);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
