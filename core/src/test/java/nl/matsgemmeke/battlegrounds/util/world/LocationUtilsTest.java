package nl.matsgemmeke.battlegrounds.util.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LocationUtilsTest {

    @Test
    @DisplayName("getCenterLocation returns given location but with modified X and Z coordinates to match the center of a block")
    void getCenterLocation() {
        World world = mock(World.class);
        Location location = new Location(world, 1.1, 2.2, 3.3, 90.0f, 0.0f);

        Location result = LocationUtils.getCenterLocation(location);

        assertThat(result.getWorld()).isEqualTo(world);
        assertThat(result.getX()).isEqualTo(1.5);
        assertThat(result.getY()).isEqualTo(2.2);
        assertThat(result.getZ()).isEqualTo(3.5);
        assertThat(result.getYaw()).isEqualTo(90.0f);
        assertThat(result.getPitch()).isEqualTo(0.0f);
    }
}
