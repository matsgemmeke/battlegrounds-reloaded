package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.DustOptionsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParticleEffectSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void createSpecReturnsRangeProfileSpecContainingValidatedValues() {
        String particle = "BLOCK_CRACK";
        Integer count = 10;
        Double offsetX = 0.1;
        Double offsetY = 0.2;
        Double offsetZ = 0.3;
        Double extra = 0.0;
        String blockData = "STONE";
        DustOptionsSpec dustOptionsSpec = new DustOptionsSpec("#ab1234", 1);

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.particle")).thenReturn(particle);
        when(yamlReader.getOptionalInt("base-route.count")).thenReturn(Optional.of(count));
        when(yamlReader.getOptionalDouble("base-route.offset-x")).thenReturn(Optional.of(offsetX));
        when(yamlReader.getOptionalDouble("base-route.offset-y")).thenReturn(Optional.of(offsetY));
        when(yamlReader.getOptionalDouble("base-route.offset-z")).thenReturn(Optional.of(offsetZ));
        when(yamlReader.getOptionalDouble("base-route.extra")).thenReturn(Optional.of(extra));
        when(yamlReader.getString("base-route.block-data")).thenReturn(blockData);
        when(yamlReader.contains("base-route.dust-options")).thenReturn(true);

        DustOptionsSpecLoader dustOptionsSpecLoader = mock(DustOptionsSpecLoader.class);
        when(dustOptionsSpecLoader.loadSpec("base-route.dust-options")).thenReturn(dustOptionsSpec);

        ParticleEffectSpecLoader specLoader = new ParticleEffectSpecLoader(yamlReader, dustOptionsSpecLoader);
        ParticleEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.particle()).isEqualTo(particle);
        assertThat(spec.count()).isEqualTo(count);
        assertThat(spec.offsetX()).isEqualTo(offsetX);
        assertThat(spec.offsetY()).isEqualTo(offsetY);
        assertThat(spec.offsetZ()).isEqualTo(offsetZ);
        assertThat(spec.extra()).isEqualTo(extra);
        assertThat(spec.blockData()).isEqualTo(blockData);
        assertThat(spec.dustOptions()).isEqualTo(dustOptionsSpec);
    }
}
