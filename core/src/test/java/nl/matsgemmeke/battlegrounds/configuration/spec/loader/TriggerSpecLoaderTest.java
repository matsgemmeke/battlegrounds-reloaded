package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void createSpecReturnsTriggerSpecContainingValidatedValues() {
        String type = "ENEMY_PROXIMITY";
        Long delay = 5L;
        Long interval = 1L;
        List<Long> offsetDelays = List.of(20L);
        Double range = 5.0;

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.type")).thenReturn(type);
        when(yamlReader.getOptionalLong("base-route.delay")).thenReturn(Optional.of(delay));
        when(yamlReader.getOptionalLong("base-route.interval")).thenReturn(Optional.of(interval));
        when(yamlReader.getOptionalLongList("base-route.offset-delays")).thenReturn(Optional.of(offsetDelays));
        when(yamlReader.getOptionalDouble("base-route.range")).thenReturn(Optional.of(range));

        TriggerSpecLoader specLoader = new TriggerSpecLoader(yamlReader);
        TriggerSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.type()).isEqualTo(type);
        assertThat(spec.delay()).isEqualTo(delay);
        assertThat(spec.interval()).isEqualTo(interval);
        assertThat(spec.offsetDelays()).isEqualTo(offsetDelays);
        assertThat(spec.range()).isEqualTo(range);
    }
}
