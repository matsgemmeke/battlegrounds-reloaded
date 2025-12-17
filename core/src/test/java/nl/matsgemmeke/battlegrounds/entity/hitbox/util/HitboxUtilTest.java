package nl.matsgemmeke.battlegrounds.entity.hitbox.util;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxUtil;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Sign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HitboxUtilTest {

    private static final Location BASE_LOCATION = new Location(null, 1, 1, 1);
    private static final double BOUNDING_BOX_SIZE = 0.5;

    @Test
    void createHitboxReturnsHitboxBasedOfGivenStaticBoundingBox() {
        StaticBoundingBox boundingBox = new StaticBoundingBox(BASE_LOCATION, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);

        Hitbox hitbox = HitboxUtil.createHitbox(boundingBox);

        assertThat(hitbox.getBaseLocation()).isEqualTo(BASE_LOCATION);
        assertThat(hitbox.getComponents()).satisfiesExactly(component -> {
           assertThat(component.type()).isEqualTo(HitboxComponentType.TORSO);
           assertThat(component.width()).isEqualTo(BOUNDING_BOX_SIZE);
           assertThat(component.height()).isEqualTo(BOUNDING_BOX_SIZE);
           assertThat(component.depth()).isEqualTo(BOUNDING_BOX_SIZE);
           assertThat(component.offsetX()).isZero();
           assertThat(component.offsetY()).isZero();
           assertThat(component.offsetZ()).isZero();
        });
    }

    @Test
    void getBedBaseLocationReturnsEmptyOptionalWhenGivenLocationBlockIsNoBed() {
        Location baseLocation = new Location(null, 1, 1, 1);

        World world = mock(World.class, Mockito.RETURNS_DEEP_STUBS);
        when(world.getBlockAt(1, 1, 1).getBlockData()).thenReturn(mock(Sign.class));

        baseLocation.setWorld(world);

        Optional<Location> locationOptional = HitboxUtil.getBedBaseLocation(baseLocation);

        assertThat(locationOptional).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({ "NORTH,0", "EAST,90", "WEST,-90", "SOUTH,180" })
    void getBedBaseLocationReturnsOptionalWithGivenLocationWithAlteredYaw(BlockFace blockFace, float expectedYaw) {
        Location baseLocation = new Location(null, 1, 1, 1);

        Bed bed = mock(Bed.class);
        when(bed.getFacing()).thenReturn(blockFace);

        World world = mock(World.class, Mockito.RETURNS_DEEP_STUBS);
        when(world.getBlockAt(baseLocation).getBlockData()).thenReturn(bed);

        baseLocation.setWorld(world);

        Optional<Location> locationOptional = HitboxUtil.getBedBaseLocation(baseLocation);

        assertThat(locationOptional).hasValueSatisfying(location -> {
            assertThat(location.getYaw()).isEqualTo(expectedYaw);
        });
    }
}
