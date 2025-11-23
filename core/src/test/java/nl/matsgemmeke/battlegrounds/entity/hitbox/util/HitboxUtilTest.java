package nl.matsgemmeke.battlegrounds.entity.hitbox.util;

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
