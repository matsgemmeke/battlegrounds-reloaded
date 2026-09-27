package nl.matsgemmeke.battlegrounds.location.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.configuration.serialization.LocationDataSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationConfigurationTest {

    private static final String WORLD = "world";
    private static final double X = 1.1;
    private static final double Y = 2.2;
    private static final double Z = 3.3;
    private static final float YAW = 90.0f;
    private static final float PITCH = 0.0f;

    @Mock
    private ConfigurationFile configurationFile;
    @Spy
    private LocationDataSerializer locationDataSerializer;
    @Mock
    private Section rootSection;
    @InjectMocks
    private LocationConfiguration locationConfiguration;

    @Test
    @DisplayName("getMainLobbyLocation returns empty optional when main lobby section does not exist")
    void getMainLobbyLocation_mainLobbyNotSet() {
        when(configurationFile.getRootSection()).thenReturn(rootSection);
        when(rootSection.getSection("main-lobby")).thenReturn(Optional.empty());

        Optional<LocationData> locationDataOptional = locationConfiguration.getMainLobbyLocation();

        assertThat(locationDataOptional).isEmpty();
    }

    @Test
    @DisplayName("getMainLobbyLocation returns optional with location data from main lobby section")
    void getMainLobbyLocation_mainLobbySet() {
        Section mainLobbySection = mock(Section.class);
        when(mainLobbySection.getString("world")).thenReturn(Optional.of(WORLD));
        when(mainLobbySection.getDouble("x")).thenReturn(Optional.of(X));
        when(mainLobbySection.getDouble("y")).thenReturn(Optional.of(Y));
        when(mainLobbySection.getDouble("z")).thenReturn(Optional.of(Z));
        when(mainLobbySection.getDouble("yaw")).thenReturn(Optional.of((double) YAW));
        when(mainLobbySection.getDouble("pitch")).thenReturn(Optional.of((double) PITCH));

        when(configurationFile.getRootSection()).thenReturn(rootSection);
        when(rootSection.getSection("main-lobby")).thenReturn(Optional.of(mainLobbySection));

        Optional<LocationData> locationDataOptional = locationConfiguration.getMainLobbyLocation();

        assertThat(locationDataOptional).hasValueSatisfying(locationData -> {
            assertThat(locationData.world()).isEqualTo(WORLD);
            assertThat(locationData.x()).isEqualTo(X);
            assertThat(locationData.y()).isEqualTo(Y);
            assertThat(locationData.z()).isEqualTo(Z);
            assertThat(locationData.yaw()).isEqualTo(YAW);
            assertThat(locationData.pitch()).isEqualTo(PITCH);
        });
    }

    @Test
    @DisplayName("setMainLobbyLocation sets location data in existing section")
    void setMainLobbyLocation_existingSection() {
        Section mainLobbySection = mock(Section.class);
        LocationData locationData = new LocationData(WORLD, X, Y, Z, YAW, PITCH);

        when(configurationFile.getRootSection()).thenReturn(rootSection);
        when(rootSection.getSection("main-lobby")).thenReturn(Optional.of(mainLobbySection));

        locationConfiguration.setMainLobbyLocation(locationData);

        verify(locationDataSerializer).serialize(locationData, mainLobbySection);
        verify(configurationFile).save();
    }

    @Test
    @DisplayName("setMainLobbyLocation sets location data in non-existing section")
    void setMainLobbyLocation_nonExistingSection() {
        Section mainLobbySection = mock(Section.class);
        LocationData locationData = new LocationData(WORLD, X, Y, Z, YAW, PITCH);

        when(configurationFile.getRootSection()).thenReturn(rootSection);
        when(rootSection.getSection("main-lobby")).thenReturn(Optional.empty());
        when(rootSection.createSection("main-lobby")).thenReturn(mainLobbySection);

        locationConfiguration.setMainLobbyLocation(locationData);

        verify(locationDataSerializer).serialize(locationData, mainLobbySection);
        verify(configurationFile).save();
    }
}
