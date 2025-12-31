package nl.matsgemmeke.battlegrounds.item.effect.source;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StaticItemEffectSourceTest {

    @Mock
    private World world;

    private StaticItemEffectSource source;

    @BeforeEach
    void setUp() {
        Location location = new Location(world, 1.0, 1.0, 1.0);

        source = new StaticItemEffectSource(location, world);
    }

    @Test
    void existsAlwaysReturnsTrue() {
        boolean exists = source.exists();

        assertThat(exists).isTrue();
    }
}
