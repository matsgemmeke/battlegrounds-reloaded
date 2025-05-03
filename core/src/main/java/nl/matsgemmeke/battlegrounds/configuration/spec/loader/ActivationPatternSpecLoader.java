package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ActivationPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.jetbrains.annotations.NotNull;

public class ActivationPatternSpecLoader {

    private static final String BURST_INTERVAL_ROUTE = "burst-interval";
    private static final String MAX_BURST_DURATION_ROUTE = "max-burst-duration";
    private static final String MIN_BURST_DURATION_ROUTE = "min-burst-duration";
    private static final String MAX_DELAY_DURATION_ROUTE = "max-delay-duration";
    private static final String MIN_DELAY_DURATION_ROUTE = "min-delay-duration";

    @NotNull
    private final YamlReader yamlReader;

    public ActivationPatternSpecLoader(@NotNull YamlReader yamlReader) {
        this.yamlReader = yamlReader;
    }

    @NotNull
    public ActivationPatternSpec loadSpec(@NotNull String baseRoute) {
        String burstIntervalRoute = this.createRoute(baseRoute, BURST_INTERVAL_ROUTE);
        String maxBurstDurationRoute = this.createRoute(baseRoute, MAX_BURST_DURATION_ROUTE);
        String minBurstDurationRoute = this.createRoute(baseRoute, MIN_BURST_DURATION_ROUTE);
        String maxDelayDurationRoute = this.createRoute(baseRoute, MAX_DELAY_DURATION_ROUTE);
        String minDelayDurationRoute = this.createRoute(baseRoute, MIN_DELAY_DURATION_ROUTE);

        Long burstInterval = new FieldSpecResolver<Long>()
                .route(burstIntervalRoute)
                .value(yamlReader.getOptionalLong(burstIntervalRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Long maxBurstDuration = new FieldSpecResolver<Long>()
                .route(maxBurstDurationRoute)
                .value(yamlReader.getOptionalLong(maxBurstDurationRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Long minBurstDuration = new FieldSpecResolver<Long>()
                .route(minBurstDurationRoute)
                .value(yamlReader.getOptionalLong(minBurstDurationRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Long maxDelayDuration = new FieldSpecResolver<Long>()
                .route(maxDelayDurationRoute)
                .value(yamlReader.getOptionalLong(maxDelayDurationRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Long minDelayDuration = new FieldSpecResolver<Long>()
                .route(minDelayDurationRoute)
                .value(yamlReader.getOptionalLong(minDelayDurationRoute).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();

        return new ActivationPatternSpec(burstInterval, maxBurstDuration, minBurstDuration, maxDelayDuration, minDelayDuration);
    }

    @NotNull
    private String createRoute(@NotNull String baseRoute, @NotNull String route) {
        return baseRoute + "." + route;
    }
}
