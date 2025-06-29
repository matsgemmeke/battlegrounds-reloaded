package nl.matsgemmeke.battlegrounds.configuration.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpreadPatternSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void createSpecReturnsSpreadPatternSpecContainingValidatedValues() {
        String type = "BUCKSHOT";
        int projectileAmount = 5;
        float horizontalSpread = 0.6f;
        float verticalSpread = 0.4f;

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.type")).thenReturn(type);
        when(yamlReader.getOptionalInt("base-route.projectile-amount")).thenReturn(Optional.of(projectileAmount));
        when(yamlReader.getOptionalFloat("base-route.horizontal-spread")).thenReturn(Optional.of(horizontalSpread));
        when(yamlReader.getOptionalFloat("base-route.vertical-spread")).thenReturn(Optional.of(verticalSpread));

        SpreadPatternSpecLoader specLoader = new SpreadPatternSpecLoader(yamlReader);
        SpreadPatternSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.type()).isEqualTo(type);
        assertThat(spec.projectileAmount()).isEqualTo(projectileAmount);
        assertThat(spec.horizontalSpread()).isEqualTo(horizontalSpread);
        assertThat(spec.verticalSpread()).isEqualTo(verticalSpread);
    }
}
