package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.PotionEffectSpec;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PotionEffectSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void createSpecReturnsPotionEffectSpecContainingValidatedValues() {
        String type = "BLINDNESS";
        Integer duration = 10;
        Integer amplifier = 1;
        Boolean ambient = true;
        Boolean particles = false;
        Boolean icon = true;

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.type")).thenReturn(type);
        when(yamlReader.getOptionalInt("base-route.duration")).thenReturn(Optional.of(duration));
        when(yamlReader.getOptionalInt("base-route.amplifier")).thenReturn(Optional.of(amplifier));
        when(yamlReader.getOptionalBoolean("base-route.ambient")).thenReturn(Optional.of(ambient));
        when(yamlReader.getOptionalBoolean("base-route.particles")).thenReturn(Optional.of(particles));
        when(yamlReader.getOptionalBoolean("base-route.icon")).thenReturn(Optional.of(icon));

        PotionEffectSpecLoader specLoader = new PotionEffectSpecLoader(yamlReader);
        PotionEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.type()).isEqualTo(type);
        assertThat(spec.duration()).isEqualTo(duration);
        assertThat(spec.amplifier()).isEqualTo(amplifier);
        assertThat(spec.ambient()).isEqualTo(ambient);
        assertThat(spec.particles()).isEqualTo(particles);
        assertThat(spec.icon()).isEqualTo(icon);
    }
}
