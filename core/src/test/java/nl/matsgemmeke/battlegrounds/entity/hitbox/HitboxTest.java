package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HitboxTest {

    private static final HitboxComponent HEAD_COMPONENT = new HitboxComponent(HitboxComponentType.HEAD, 0.4, 0.4, 0.4, 0.0, 1.4, 0.0);
    private static final HitboxComponent BODY_COMPONENT = new HitboxComponent(HitboxComponentType.TORSO, 0.7, 0.4, 0.2, 0.0, 0.7, 0.0);
    private static final HitboxComponent LEGS_COMPONENT = new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.4, 0.2, 0.0, 0.0, 0.0);
    /**
     * A relative hitbox setup similar to a player's.
     */
    private static final RelativeHitbox RELATIVE_HITBOX = new RelativeHitbox(Set.of(HEAD_COMPONENT, BODY_COMPONENT, LEGS_COMPONENT));

    @ParameterizedTest(name = "X: {0}, Y: {1}, Z: {2}, Box yaw: {3}")
    @CsvSource({
            // These are all variables that will barely hit the side of the torso hitbox
            "0.20,1.0,0.00,0.0",
            "0.185,1.0,0.075,22.5",
            "0.141,1.0,0.141,45.0",
            "0.075,1.0,0.185,67.5",
            "0.00,1.0,0.20,90.0"
    })
    void getIntersectedHitboxComponentReturnsOptionalWithInterestedHitboxComponent(double x, double y, double z, float boxYaw) {
        World world = mock(World.class);
        Location location = new Location(world, x, y, z);
        Location baseLocation = new Location(world, 0, 0, 0, boxYaw, 0);

        Hitbox hitbox = new Hitbox(baseLocation, RELATIVE_HITBOX);
        Optional<HitboxComponent> hitboxComponentOptional = hitbox.getIntersectedHitboxComponent(location);

        assertThat(hitboxComponentOptional).hasValueSatisfying(hitboxComponent -> {
            assertThat(hitboxComponent.type()).isEqualTo(HitboxComponentType.TORSO);
        });
    }

    @Test
    void getIntersectedHitboxComponentReturnsEmptyOptionalWhenGivenLocationIntersectsNoComponents() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);
        Location baseLocation = new Location(world, 0, 0, 0, 0, 0);

        Hitbox hitbox = new Hitbox(baseLocation, RELATIVE_HITBOX);
        Optional<HitboxComponent> hitboxComponentOptional = hitbox.getIntersectedHitboxComponent(location);

        assertThat(hitboxComponentOptional).isEmpty();
    }

    @ParameterizedTest(name = "X: {0}, Y: {1}, Z: {2}")
    @CsvSource({
            "1.0,0.0,0.0",
            "0.0,3.0,0.0",
            "0.0,0.0,1.0"
    })
    void intersectsHitboxReturnsFalseWhenAnyAxisIsOutsideGivenPositionBox(double x, double y, double z) {
        World world = mock(World.class);
        Location location = new Location(world, x, y, z);
        Location baseLocation = new Location(world, 0, 0, 0, 0, 0);

        Hitbox hitbox = new Hitbox(baseLocation, RELATIVE_HITBOX);
        boolean intersects = hitbox.intersects(location);

        assertThat(intersects).isFalse();
    }

    @Test
    void intersectsHitboxReturnsTrueWhenAllAxesAreInsideGivenBox() {
        World world = mock(World.class);
        Location location = new Location(world, 9.9, 10.1, 10.1);
        Location baseLocation = new Location(world, 10.0, 10.0, 10.0);

        Hitbox hitbox = new Hitbox(baseLocation, RELATIVE_HITBOX);
        boolean intersects = hitbox.intersects(location);

        assertThat(intersects).isTrue();
    }
}
