package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void createSpecReturnsTriggerSpecContainingValidatedValues() {
        String type = "ENEMY_PROXIMITY";
        Double checkRange = 5.0;
        Long checkInterval = 2L;
        Long delayUntilActivation = 20L;

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.type")).thenReturn(type);
        when(yamlReader.getOptionalDouble("base-route.check-range")).thenReturn(Optional.of(checkRange));
        when(yamlReader.getOptionalLong("base-route.check-interval")).thenReturn(Optional.of(checkInterval));
        when(yamlReader.getOptionalLong("base-route.delay-until-activation")).thenReturn(Optional.of(delayUntilActivation));

        TriggerSpecLoader specLoader = new TriggerSpecLoader(yamlReader);
        TriggerSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.type()).isEqualTo(type);
        assertThat(spec.checkRange()).isEqualTo(checkRange);
        assertThat(spec.checkInterval()).isEqualTo(checkInterval);
        assertThat(spec.delayUntilActivation()).isEqualTo(delayUntilActivation);
    }
}
