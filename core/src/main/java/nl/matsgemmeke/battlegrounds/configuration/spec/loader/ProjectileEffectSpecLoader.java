package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.OneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredIfFieldEqualsValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProjectileEffectSpecLoader {

    private static final Set<String> ALLOWED_PROJECTILE_EFFECT_TYPES = Set.of("BOUNCE", "SOUND", "STICK", "TRAIL");

    private static final String TYPE_ROUTE = "type";
    private static final String DELAY_ROUTE = "delay";
    private static final String INTERVALS_ROUTE = "intervals";
    private static final String SOUNDS_ROUTE = "sounds";
    private static final String HORIZONTAL_FRICTION_ROUTE = "horizontal-friction";
    private static final String VERTICAL_FRICTION_ROUTE = "vertical-friction";
    private static final String MAX_ACTIVATIONS_ROUTE = "max-activations";
    private static final String PARTICLE_EFFECT_ROUTE = "particle-effect";
    private static final String TRIGGERS_ROUTE = "triggers";

    @NotNull
    private ParticleEffectSpecLoader particleEffectSpecLoader;
    @NotNull
    private TriggerSpecLoader triggerSpecLoader;
    @NotNull
    private final YamlReader yamlReader;

    public ProjectileEffectSpecLoader(@NotNull YamlReader yamlReader, @NotNull ParticleEffectSpecLoader particleEffectSpecLoader, @NotNull TriggerSpecLoader triggerSpecLoader) {
        this.yamlReader = yamlReader;
        this.particleEffectSpecLoader = particleEffectSpecLoader;
        this.triggerSpecLoader = triggerSpecLoader;
    }

    @NotNull
    public ProjectileEffectSpec loadSpec(@NotNull String baseRoute) {
        String typeRoute = this.createRoute(baseRoute, TYPE_ROUTE);
        String delayRoute = this.createRoute(baseRoute, DELAY_ROUTE);
        String intervalsRoute = this.createRoute(baseRoute, INTERVALS_ROUTE);
        String soundsRoute = this.createRoute(baseRoute, SOUNDS_ROUTE);
        String horizontalFrictionRoute = this.createRoute(baseRoute, HORIZONTAL_FRICTION_ROUTE);
        String verticalFrictionRoute = this.createRoute(baseRoute, VERTICAL_FRICTION_ROUTE);
        String maxActivationsRoute = this.createRoute(baseRoute, MAX_ACTIVATIONS_ROUTE);
        String particleEffectRoute = this.createRoute(baseRoute, PARTICLE_EFFECT_ROUTE);
        String triggersRoute = this.createRoute(baseRoute, TRIGGERS_ROUTE);

        String type = new FieldSpecResolver<String>()
                .route(typeRoute)
                .value(yamlReader.getString(typeRoute))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_PROJECTILE_EFFECT_TYPES))
                .resolve();
        Long delay = new FieldSpecResolver<Long>()
                .route(delayRoute)
                .value(yamlReader.getOptionalLong(delayRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("COUNTDOWN", "TRAIL")))
                .resolve();
        List<Long> intervals = new FieldSpecResolver<List<Long>>()
                .route(intervalsRoute)
                .value(yamlReader.getOptionalLongList(intervalsRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("COUNTDOWN", "TRAIL")))
                .resolve();
        String sounds = new FieldSpecResolver<String>()
                .route(soundsRoute)
                .value(yamlReader.getString(soundsRoute))
                .resolve();
        Double horizontalFriction = new FieldSpecResolver<Double>()
                .route(horizontalFrictionRoute)
                .value(yamlReader.getOptionalDouble(horizontalFrictionRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "BOUNCE"))
                .resolve();
        Double verticalFriction = new FieldSpecResolver<Double>()
                .route(verticalFrictionRoute)
                .value(yamlReader.getOptionalDouble(verticalFrictionRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, "BOUNCE"))
                .resolve();
        Integer maxActivations = new FieldSpecResolver<Integer>()
                .route(maxActivationsRoute)
                .value(yamlReader.getOptionalInt(maxActivationsRoute).orElse(null))
                .validate(new RequiredIfFieldEqualsValidator<>(typeRoute, type, Set.of("BOUNCE", "TRAIL")))
                .resolve();

        ParticleEffectSpec particleEffectSpec = particleEffectSpecLoader.loadSpec(particleEffectRoute);
        List<TriggerSpec> triggerSpecs = new ArrayList<>();

        for (String key : yamlReader.getRoutes(triggersRoute)) {
            String triggerRoute = this.createRoute(baseRoute, TRIGGERS_ROUTE + "." + key);
            TriggerSpec triggerSpec = triggerSpecLoader.loadSpec(triggerRoute);

            triggerSpecs.add(triggerSpec);
        }

        return new ProjectileEffectSpec(type, delay, intervals, sounds, horizontalFriction, verticalFriction, maxActivations, particleEffectSpec, triggerSpecs);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
