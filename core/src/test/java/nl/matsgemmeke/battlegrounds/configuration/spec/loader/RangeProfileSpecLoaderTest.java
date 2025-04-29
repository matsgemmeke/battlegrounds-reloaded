package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RangeProfileSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    private YamlReader yamlReader;

    @BeforeEach
    public void setUp() {
        yamlReader = mock(YamlReader.class);
    }

    @Test
    public void createSpecReturnsRangeProfileSpecContainingValidatedValues() {
        Double shortRangeDamage = 35.0;
        Double shortRangeDistance = 10.0;
        Double mediumRangeDamage = 25.0;
        Double mediumRangeDistance = 20.0;
        Double longRangeDamage = 15.0;
        Double longRangeDistance = 30.0;

        when(yamlReader.getOptionalDouble("base-route.short-range.damage")).thenReturn(Optional.of(shortRangeDamage));
        when(yamlReader.getOptionalDouble("base-route.short-range.distance")).thenReturn(Optional.of(shortRangeDistance));
        when(yamlReader.getOptionalDouble("base-route.medium-range.damage")).thenReturn(Optional.of(mediumRangeDamage));
        when(yamlReader.getOptionalDouble("base-route.medium-range.distance")).thenReturn(Optional.of(mediumRangeDistance));
        when(yamlReader.getOptionalDouble("base-route.long-range.damage")).thenReturn(Optional.of(longRangeDamage));
        when(yamlReader.getOptionalDouble("base-route.long-range.distance")).thenReturn(Optional.of(longRangeDistance));

        RangeProfileSpecLoader specLoader = new RangeProfileSpecLoader(yamlReader, BASE_ROUTE);
        RangeProfileSpec spec = specLoader.loadSpec();

        assertThat(spec.shortRangeDamage()).isEqualTo(shortRangeDamage);
        assertThat(spec.shortRangeDistance()).isEqualTo(shortRangeDistance);
        assertThat(spec.mediumRangeDamage()).isEqualTo(mediumRangeDamage);
        assertThat(spec.mediumRangeDistance()).isEqualTo(mediumRangeDistance);
        assertThat(spec.longRangeDamage()).isEqualTo(longRangeDamage);
        assertThat(spec.longRangeDistance()).isEqualTo(longRangeDistance);
    }
}
