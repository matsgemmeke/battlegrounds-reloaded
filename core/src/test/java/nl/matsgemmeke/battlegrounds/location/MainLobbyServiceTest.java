package nl.matsgemmeke.battlegrounds.location;

import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.location.configuration.LocationConfiguration;
import nl.matsgemmeke.battlegrounds.util.world.LocationMapper;
import org.bukkit.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainLobbyServiceTest {

    @Mock
    private LocationConfiguration locationConfiguration;
    @Mock
    private LocationMapper locationMapper;
    @InjectMocks
    private MainLobbyService mainLobbyService;

    @Test
    @DisplayName("getMainLobbyLocation returns empty optional when language configuration has no saved main lobby")
    void getMainLobbyLocation_notSaved() {
        when(locationConfiguration.getMainLobbyLocation()).thenReturn(Optional.empty());

        Optional<Location> locationOptional = mainLobbyService.getMainLobbyLocation();

        assertThat(locationOptional).isEmpty();
    }

    @Test
    @DisplayName("getMainLobbyLocation returns optional with main lobby location when language configuration has a saved main lobby")
    void getMainLobbyLocation_saved() {
        LocationData locationData = new LocationData(null, null, null, null, null, null);
        Location location = new Location(null, 0, 0, 0);

        when(locationConfiguration.getMainLobbyLocation()).thenReturn(Optional.of(locationData));
        when(locationMapper.toLocation(locationData)).thenReturn(location);

        Optional<Location> locationOptional = mainLobbyService.getMainLobbyLocation();

        assertThat(locationOptional).hasValue(location);
    }
}
