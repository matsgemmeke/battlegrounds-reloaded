package nl.matsgemmeke.battlegrounds.item.effect;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class StaticSourceTest {

    private Location location;
    private World world;

    @BeforeEach
    public void setUp() {
        world = mock(World.class);
        location = new Location(world, 1.0, 1.0, 1.0);
    }

    @Test
    public void existsAlwaysReturnsFalse() {
        StaticSource source = new StaticSource(location, world);
        boolean exists = source.exists();

        assertThat(exists).isFalse();
    }

    @Test
    public void getVelocityReturnsZeroVector() {
        StaticSource source = new StaticSource(location, world);
        Vector velocity = source.getVelocity();

        assertThat(velocity.getX()).isZero();
        assertThat(velocity.getY()).isZero();
        assertThat(velocity.getZ()).isZero();
    }
}
