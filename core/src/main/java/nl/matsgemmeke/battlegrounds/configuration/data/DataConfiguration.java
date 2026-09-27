package nl.matsgemmeke.battlegrounds.configuration.data;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.configuration.serialization.LocationDataSerializer;

import java.util.Optional;

public class DataConfiguration {

    private static final String MAIN_LOBBY_PATH = "main-lobby";

    private final ConfigurationFile configurationFile;
    private final LocationDataSerializer locationDataSerializer;

    @Inject
    public DataConfiguration(@Named("data") ConfigurationFile configurationFile, LocationDataSerializer locationDataSerializer) {
        this.configurationFile = configurationFile;
        this.locationDataSerializer = locationDataSerializer;
    }

    public Optional<LocationData> getMainLobbyLocation() {
        return configurationFile.getRootSection().getSection(MAIN_LOBBY_PATH).map(locationDataSerializer::deserialize);
    }

    public void setMainLobbyLocation(LocationData locationData) {
        Section rootSection = configurationFile.getRootSection();
        Section mainLobbySection = rootSection.getSection(MAIN_LOBBY_PATH).orElseGet(() -> rootSection.createSection(MAIN_LOBBY_PATH));

        locationDataSerializer.serialize(locationData, mainLobbySection);
        configurationFile.save();
    }
}
