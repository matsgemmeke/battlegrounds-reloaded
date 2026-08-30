package nl.matsgemmeke.battlegrounds.arena.loading.element;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn.SpawnPointData;
import nl.matsgemmeke.battlegrounds.arena.map.element.SpawnPoint;
import nl.matsgemmeke.battlegrounds.util.world.LocationMapper;
import org.bukkit.Location;

public class SpawnPointFactory implements ElementFactory<SpawnPointData, SpawnPoint> {

    private final LocationMapper locationMapper;

    @Inject
    public SpawnPointFactory(LocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    @Override
    public SpawnPoint create(SpawnPointData data) {
        int elementId = data.getElementId();
        int teamId = data.getTeamId();
        Location location = locationMapper.toLocation(data.getLocationData());

        return new SpawnPoint(elementId, teamId, location);
    }
}
