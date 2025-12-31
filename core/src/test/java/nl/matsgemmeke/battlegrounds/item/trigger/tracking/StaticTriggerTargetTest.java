package nl.matsgemmeke.battlegrounds.item.trigger.tracking;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StaticTriggerTargetTest {

    private static final Location LOCATION = new Location(null, 1, 1, 1);

    @Mock
    private World world;

    private StaticTriggerTarget triggerTarget;

    @BeforeEach
    void setUp() {
        triggerTarget = new StaticTriggerTarget(LOCATION, world);
    }

    @Test
    void existsAlwaysReturnsTrue() {
        boolean exists = triggerTarget.exists();

        assertThat(exists).isTrue();
    }

    @Test
    void getLocationReturnsStaticLocation() {
        Location result = triggerTarget.getLocation();

        assertThat(result).isEqualTo(LOCATION);
    }

    @Test
    void getVelocityReturnsZeroVector() {
        Vector result = triggerTarget.getVelocity();

        assertThat(result.isZero()).isTrue();
    }

    @Test
    void getWorldReturnsStaticWorld() {
        World result = triggerTarget.getWorld();

        assertThat(result).isEqualTo(world);
    }
}
