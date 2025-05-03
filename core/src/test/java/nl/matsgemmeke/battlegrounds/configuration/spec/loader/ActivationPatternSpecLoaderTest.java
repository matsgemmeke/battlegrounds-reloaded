package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ActivationPatternSpec;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActivationPatternSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void createSpecReturnsActivationPatternSpecContainingValidatedValues() {
        Long burstInterval = 2L;
        Long maxBurstDuration = 200L;
        Long minBurstDuration = 100L;
        Long maxDelayDuration = 20L;
        Long minDelayDuration = 10L;

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getOptionalLong("base-route.burst-interval")).thenReturn(Optional.of(burstInterval));
        when(yamlReader.getOptionalLong("base-route.max-burst-duration")).thenReturn(Optional.of(maxBurstDuration));
        when(yamlReader.getOptionalLong("base-route.min-burst-duration")).thenReturn(Optional.of(minBurstDuration));
        when(yamlReader.getOptionalLong("base-route.max-delay-duration")).thenReturn(Optional.of(maxDelayDuration));
        when(yamlReader.getOptionalLong("base-route.min-delay-duration")).thenReturn(Optional.of(minDelayDuration));

        ActivationPatternSpecLoader activationPatternSpecLoader = new ActivationPatternSpecLoader(yamlReader);
        ActivationPatternSpec activationPatternSpec = activationPatternSpecLoader.loadSpec(BASE_ROUTE);

        assertThat(activationPatternSpec.burstInterval()).isEqualTo(burstInterval);
        assertThat(activationPatternSpec.maxBurstDuration()).isEqualTo(maxBurstDuration);
        assertThat(activationPatternSpec.minBurstDuration()).isEqualTo(minBurstDuration);
        assertThat(activationPatternSpec.maxDelayDuration()).isEqualTo(maxDelayDuration);
        assertThat(activationPatternSpec.minDelayDuration()).isEqualTo(minDelayDuration);
    }
}
