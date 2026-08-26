package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementDataReader;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.configuration.serialization.LocationDataSerializer;

public class SpawnPointDataReader implements ElementDataReader {

    private final LocationDataSerializer locationDataSerializer;

    @Inject
    public SpawnPointDataReader(LocationDataSerializer locationDataSerializer) {
        this.locationDataSerializer = locationDataSerializer;
    }

    @Override
    public ElementData read(Section section) {
        Integer elementId = section.getInt("element-id").orElse(null);
        LocationData location = section.getSection("location").map(locationDataSerializer::deserialize).orElse(null);
        Integer teamId = section.getInt("team-id").orElse(null);

        SpawnPointData spawnPointData = new SpawnPointData();
        spawnPointData.setElementId(elementId);
        spawnPointData.setLocation(location);
        spawnPointData.setTeamId(teamId);
        return spawnPointData;
    }
}
