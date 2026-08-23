package nl.matsgemmeke.battlegrounds.util.world;

import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationMapperTest {

    private static final String WORLD = "world";
    private static final double X = 1.1;
    private static final double Y = 2.2;
    private static final double Z = 3.3;
    private static final float YAW = 90.0f;
    private static final float PITCH = 10.0f;

    @Mock
    private WorldProvider worldProvider;
    @InjectMocks
    private LocationMapper locationMapper;

    @Test
    @DisplayName("toLocation throws InvalidLocationException when given LocationData has an invalid world")
    void toLocation_invalidWorld() {
        LocationData locationData = new LocationData(WORLD, X, Y, Z, YAW, PITCH);

        when(worldProvider.getWorld(WORLD)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationMapper.toLocation(locationData))
                .isInstanceOf(InvalidLocationException.class)
                .hasMessage("Cannot convert given LocationData because its world does not exist");
    }

    @Test
    @DisplayName("toLocation returns Location object with variables from given LocationData")
    void toLocation_successful() {
        LocationData locationData = new LocationData(WORLD, X, Y, Z, YAW, PITCH);
        World world = mock(World.class);

        when(worldProvider.getWorld(WORLD)).thenReturn(Optional.of(world));

        Location location = locationMapper.toLocation(locationData);

        assertThat(location.getWorld()).isEqualTo(world);
        assertThat(location.getX()).isEqualTo(X);
        assertThat(location.getY()).isEqualTo(Y);
        assertThat(location.getZ()).isEqualTo(Z);
        assertThat(location.getYaw()).isEqualTo(YAW);
        assertThat(location.getPitch()).isEqualTo(PITCH);
    }

    @Test
    @DisplayName("toLocationData throws InvalidLocationException when given Location has no world")
    void toLocationData_nullWorld() {
        Location location = new Location(null, X, Y, Z, YAW, PITCH);

        assertThatThrownBy(() -> locationMapper.toLocationData(location))
                .isInstanceOf(InvalidLocationException.class)
                .hasMessage("Cannot convert given Location because its world is null");
    }

    @Test
    @DisplayName("toLocationData returns LocationData with variables from given Location")
    void toLocationData_successful() {
        World world = mock(World.class);
        when(world.getName()).thenReturn(WORLD);

        Location location = new Location(world, X, Y, Z, YAW, PITCH);

        LocationData locationData = locationMapper.toLocationData(location);

        assertThat(locationData.world()).isEqualTo(WORLD);
        assertThat(locationData.x()).isEqualTo(X);
        assertThat(locationData.y()).isEqualTo(Y);
        assertThat(locationData.z()).isEqualTo(Z);
        assertThat(locationData.yaw()).isEqualTo(YAW);
        assertThat(locationData.pitch()).isEqualTo(PITCH);
    }
}
