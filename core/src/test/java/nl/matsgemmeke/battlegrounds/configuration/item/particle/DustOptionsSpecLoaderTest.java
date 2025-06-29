package nl.matsgemmeke.battlegrounds.configuration.item.particle;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DustOptionsSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void createSpecReturnsDustOptionsSpecContainingValidatedValues() {
        String color = "#ab1234";
        Integer size = 1;

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.color")).thenReturn(color);
        when(yamlReader.getOptionalInt("base-route.size")).thenReturn(Optional.of(size));

        DustOptionsSpecLoader specLoader = new DustOptionsSpecLoader(yamlReader);
        DustOptionsSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.color()).isEqualTo(color);
        assertThat(spec.size()).isEqualTo(size);
    }
}
