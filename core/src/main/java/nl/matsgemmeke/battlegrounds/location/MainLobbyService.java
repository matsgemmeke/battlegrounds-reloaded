package nl.matsgemmeke.battlegrounds.location;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.location.configuration.LocationConfiguration;
import nl.matsgemmeke.battlegrounds.util.world.LocationMapper;
import org.bukkit.Location;

import java.util.Optional;

public class MainLobbyService {

    private final LocationConfiguration locationConfiguration;
    private final LocationMapper locationMapper;

    @Inject
    public MainLobbyService(LocationConfiguration locationConfiguration, LocationMapper locationMapper) {
        this.locationConfiguration = locationConfiguration;
        this.locationMapper = locationMapper;
    }

    public Optional<Location> getMainLobbyLocation() {
        return locationConfiguration.getMainLobbyLocation().map(locationMapper::toLocation);
    }
}
