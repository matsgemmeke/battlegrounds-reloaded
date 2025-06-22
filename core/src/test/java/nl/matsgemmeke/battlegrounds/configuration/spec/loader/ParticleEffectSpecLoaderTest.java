package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.DustOptionsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParticleEffectSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";
    private static final int COUNT = 10;
    private static final double OFFSET_X = 0.1;
    private static final double OFFSET_Y = 0.2;
    private static final double OFFSET_Z = 0.3;
    private static final double EXTRA = 0.0;

    private DustOptionsSpecLoader dustOptionsSpecLoader;
    private YamlReader yamlReader;

    @BeforeEach
    public void setUp() {
        dustOptionsSpecLoader = mock(DustOptionsSpecLoader.class);

        yamlReader = mock(YamlReader.class);
        when(yamlReader.getOptionalInt("base-route.count")).thenReturn(Optional.of(COUNT));
        when(yamlReader.getOptionalDouble("base-route.offset-x")).thenReturn(Optional.of(OFFSET_X));
        when(yamlReader.getOptionalDouble("base-route.offset-y")).thenReturn(Optional.of(OFFSET_Y));
        when(yamlReader.getOptionalDouble("base-route.offset-z")).thenReturn(Optional.of(OFFSET_Z));
        when(yamlReader.getOptionalDouble("base-route.extra")).thenReturn(Optional.of(EXTRA));
    }

    @Test
    public void createSpecReturnsParticleEffectSpecWithoutExtraOptions() {
        String particle = "FLAME";

        when(yamlReader.contains("base-route.block-data")).thenReturn(false);
        when(yamlReader.contains("base-route.dust-options")).thenReturn(false);
        when(yamlReader.getString("base-route.particle")).thenReturn(particle);

        ParticleEffectSpecLoader specLoader = new ParticleEffectSpecLoader(yamlReader, dustOptionsSpecLoader);
        ParticleEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.particle()).isEqualTo(particle);
        assertThat(spec.count()).isEqualTo(COUNT);
        assertThat(spec.offsetX()).isEqualTo(OFFSET_X);
        assertThat(spec.offsetY()).isEqualTo(OFFSET_Y);
        assertThat(spec.offsetZ()).isEqualTo(OFFSET_Z);
        assertThat(spec.extra()).isEqualTo(EXTRA);
        assertThat(spec.blockData()).isNull();
        assertThat(spec.dustOptions()).isNull();
    }

    @Test
    public void createSpecReturnsParticleEffectSpecWithBlockData() {
        String particle = "BLOCK_CRACK";
        String blockData = "STONE";

        when(yamlReader.contains("base-route.block-data")).thenReturn(true);
        when(yamlReader.contains("base-route.dust-options")).thenReturn(false);
        when(yamlReader.getString("base-route.particle")).thenReturn(particle);
        when(yamlReader.getString("base-route.block-data")).thenReturn(blockData);

        ParticleEffectSpecLoader specLoader = new ParticleEffectSpecLoader(yamlReader, dustOptionsSpecLoader);
        ParticleEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.particle()).isEqualTo(particle);
        assertThat(spec.count()).isEqualTo(COUNT);
        assertThat(spec.offsetX()).isEqualTo(OFFSET_X);
        assertThat(spec.offsetY()).isEqualTo(OFFSET_Y);
        assertThat(spec.offsetZ()).isEqualTo(OFFSET_Z);
        assertThat(spec.extra()).isEqualTo(EXTRA);
        assertThat(spec.blockData()).isEqualTo(blockData);
        assertThat(spec.dustOptions()).isNull();
    }

    @Test
    public void createSpecReturnsParticleEffectSpecWithoutBlockDataWhenParticleTypeDoesNotSupportIt() {
        String particle = "FLAME";
        String blockData = "STONE";

        when(yamlReader.contains("base-route.block-data")).thenReturn(true);
        when(yamlReader.contains("base-route.dust-options")).thenReturn(false);
        when(yamlReader.getString("base-route.particle")).thenReturn(particle);
        when(yamlReader.getString("base-route.block-data")).thenReturn(blockData);

        ParticleEffectSpecLoader specLoader = new ParticleEffectSpecLoader(yamlReader, dustOptionsSpecLoader);
        ParticleEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.particle()).isEqualTo(particle);
        assertThat(spec.count()).isEqualTo(COUNT);
        assertThat(spec.offsetX()).isEqualTo(OFFSET_X);
        assertThat(spec.offsetY()).isEqualTo(OFFSET_Y);
        assertThat(spec.offsetZ()).isEqualTo(OFFSET_Z);
        assertThat(spec.extra()).isEqualTo(EXTRA);
        assertThat(spec.blockData()).isNull();
        assertThat(spec.dustOptions()).isNull();
    }

    @Test
    public void createSpecReturnsParticleEffectSpecWithDustOptions() {
        String particle = "REDSTONE";
        DustOptionsSpec dustOptionsSpec = new DustOptionsSpec("#ab1234", 1);

        when(yamlReader.contains("base-route.block-data")).thenReturn(false);
        when(yamlReader.contains("base-route.dust-options")).thenReturn(true);
        when(yamlReader.getString("base-route.particle")).thenReturn(particle);

        when(dustOptionsSpecLoader.loadSpec("base-route.dust-options")).thenReturn(dustOptionsSpec);

        ParticleEffectSpecLoader specLoader = new ParticleEffectSpecLoader(yamlReader, dustOptionsSpecLoader);
        ParticleEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.particle()).isEqualTo(particle);
        assertThat(spec.count()).isEqualTo(COUNT);
        assertThat(spec.offsetX()).isEqualTo(OFFSET_X);
        assertThat(spec.offsetY()).isEqualTo(OFFSET_Y);
        assertThat(spec.offsetZ()).isEqualTo(OFFSET_Z);
        assertThat(spec.extra()).isEqualTo(EXTRA);
        assertThat(spec.blockData()).isNull();
        assertThat(spec.dustOptions()).isEqualTo(dustOptionsSpec);
    }

    @Test
    public void createSpecReturnsParticleEffectSpecWithoutDustOptionsWhenParticleTypeDoesNotSupportIt() {
        String particle = "FLAME";

        when(yamlReader.contains("base-route.block-data")).thenReturn(false);
        when(yamlReader.contains("base-route.dust-options")).thenReturn(true);
        when(yamlReader.getString("base-route.particle")).thenReturn(particle);

        ParticleEffectSpecLoader specLoader = new ParticleEffectSpecLoader(yamlReader, dustOptionsSpecLoader);
        ParticleEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.particle()).isEqualTo(particle);
        assertThat(spec.count()).isEqualTo(COUNT);
        assertThat(spec.offsetX()).isEqualTo(OFFSET_X);
        assertThat(spec.offsetY()).isEqualTo(OFFSET_Y);
        assertThat(spec.offsetZ()).isEqualTo(OFFSET_Z);
        assertThat(spec.extra()).isEqualTo(EXTRA);
        assertThat(spec.blockData()).isNull();
        assertThat(spec.dustOptions()).isNull();
    }
}
