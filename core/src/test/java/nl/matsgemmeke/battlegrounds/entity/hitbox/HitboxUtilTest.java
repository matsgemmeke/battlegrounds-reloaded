package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HitboxUtilTest {

    private static final double HEIGHT = 0.5;
    private static final double WIDTH = 0.5;
    private static final double DEPTH = 0.5;
    private static final double OFFSET = 0.0;

    @ParameterizedTest
    @CsvSource({
            "11.0,10.0,10.0",
            "10.0,11.0,10.0",
            "10.0,10.0,11.0"
    })
    void intersectsHitboxReturnsFalseWhenAnyAxisIsOutsideGivenPositionBox(double x, double y, double z) {
        World world = mock(World.class);
        Location location = new Location(world, x, y, z);
        Location boxLocation = new Location(world, 10.0, 10.0, 10.0);
        PositionHitbox positionHitbox = this.createPositionHitbox();

        boolean intersects = HitboxUtil.intersectsHitbox(location, boxLocation, positionHitbox);

        assertThat(intersects).isFalse();
    }

    @Test
    void intersectsHitboxReturnsTrueWhenAllAxesAreInsideGivenBox() {
        World world = mock(World.class);
        Location location = new Location(world, 9.9, 10.1, 10.1);
        Location boxLocation = new Location(world, 10.0, 10.0, 10.0);
        PositionHitbox positionHitbox = this.createPositionHitbox();

        boolean intersects = HitboxUtil.intersectsHitbox(location, boxLocation, positionHitbox);

        assertThat(intersects).isTrue();
    }

    private PositionHitbox createPositionHitbox() {
        HitboxComponent hitboxComponent = new HitboxComponent(HitboxComponentType.TORSO, HEIGHT, WIDTH, DEPTH, OFFSET, OFFSET, OFFSET);

        return new PositionHitbox(Set.of(hitboxComponent));
    }
}
