package nl.matsgemmeke.battlegrounds.configuration.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpecLoader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.validation.OneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProjectileSpecLoader {

    private static final List<String> ALLOWED_TYPE_VALUES = List.of("BULLET");

    private static final String TYPE_ROUTE = "type";
    private static final String SHOT_SOUNDS_ROUTE = "shot-sounds";
    private static final String TRAJECTORY_PARTICLE_EFFECT_ROUTE = "trajectory-particle-effect";

    @NotNull
    private final ParticleEffectSpecLoader particleEffectSpecLoader;
    @NotNull
    private final YamlReader yamlReader;

    public ProjectileSpecLoader(@NotNull YamlReader yamlReader, @NotNull ParticleEffectSpecLoader particleEffectSpecLoader) {
        this.yamlReader = yamlReader;
        this.particleEffectSpecLoader = particleEffectSpecLoader;
    }

    @NotNull
    public ProjectileSpec loadSpec(@NotNull String baseRoute) {
        String typeRoute = this.createRoute(baseRoute, TYPE_ROUTE);
        String shotSoundsRoute = this.createRoute(baseRoute, SHOT_SOUNDS_ROUTE);
        String trajectoryParticleEffectRoute = this.createRoute(baseRoute, TRAJECTORY_PARTICLE_EFFECT_ROUTE);

        String type = new FieldSpecResolver<String>()
                .route(typeRoute)
                .value(yamlReader.getString(typeRoute))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_TYPE_VALUES))
                .resolve();
        String shotSounds = new FieldSpecResolver<String>()
                .route(shotSoundsRoute)
                .value(yamlReader.getString(shotSoundsRoute))
                .resolve();

        ParticleEffectSpec trajectoryParticleEffectSpec = particleEffectSpecLoader.loadSpec(trajectoryParticleEffectRoute);

        return new ProjectileSpec(type, shotSounds, trajectoryParticleEffectSpec);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
