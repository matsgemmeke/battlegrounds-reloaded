package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemEffectSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void loadSpecReturnsItemEffectSpecContainingValidatedValues() {
        String type = "COMBUSTION";
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.type")).thenReturn(type);

        RangeProfileSpecLoader rangeProfileSpecLoader = mock(RangeProfileSpecLoader.class);
        when(rangeProfileSpecLoader.loadSpec("base-route.range")).thenReturn(rangeProfileSpec);

        ItemEffectSpecLoader specLoader = new ItemEffectSpecLoader(yamlReader, rangeProfileSpecLoader);
        ItemEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.type()).isEqualTo(type);
        assertThat(spec.rangeProfile()).isEqualTo(rangeProfileSpec);
    }
}
