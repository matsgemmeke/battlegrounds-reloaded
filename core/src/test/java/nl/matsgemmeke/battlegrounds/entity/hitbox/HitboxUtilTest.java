package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class HitboxUtilTest {

    private static final double HEIGHT = 2.0;
    private static final double WIDTH = 0.5;

    @ParameterizedTest
    @CsvSource({
            "11.0,10.0,10.0",
            "10.0,13.0,10.0",
            "10.0,10.0,11.0"
    })
    void intersectsBoxReturnsFalseWhenAnyAxisIsOutsideGivenBox(double x, double y, double z) {
        World world = mock(World.class);
        Location location = new Location(world, x, y, z);
        Location boxLocation = new Location(world, 10.0, 10.0, 10.0);

        boolean intersects = HitboxUtil.intersectsBox(location, boxLocation, HEIGHT, WIDTH);

        assertThat(intersects).isFalse();
    }

    @Test
    void intersectsBoxReturnsTrueWhenAllAxesAreInsideGivenBox() {
        World world = mock(World.class);
        Location location = new Location(world, 9.9, 11.0, 10.1);
        Location boxLocation = new Location(world, 10.0, 10.0, 10.0);

        boolean intersects = HitboxUtil.intersectsBox(location, boxLocation, HEIGHT, WIDTH);

        assertThat(intersects).isTrue();
    }
}
