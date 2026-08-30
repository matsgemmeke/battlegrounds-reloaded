package nl.matsgemmeke.battlegrounds.arena.loading.element;

import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn.SpawnPointData;
import nl.matsgemmeke.battlegrounds.arena.map.element.SpawnPoint;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.util.world.LocationMapper;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpawnPointFactoryTest {

    private static final int ELEMENT_ID = 1;
    private static final String ELEMENT_TYPE = "SPAWN_POINT";
    private static final int TEAM_ID = 2;

    @Mock
    private LocationMapper locationMapper;
    @InjectMocks
    private SpawnPointFactory spawnPointFactory;

    @Test
    void create() {
        LocationData locationData = new LocationData(null, 0, 0, 0, 0, 0);
        Location location = new Location(null, 0, 0, 0, 0, 0);

        SpawnPointData spawnPointData = new SpawnPointData();
        spawnPointData.setElementId(ELEMENT_ID);
        spawnPointData.setElementType(ELEMENT_TYPE);
        spawnPointData.setLocationData(locationData);
        spawnPointData.setTeamId(TEAM_ID);

        when(locationMapper.toLocation(locationData)).thenReturn(location);

        SpawnPoint spawnPoint = spawnPointFactory.create(spawnPointData);

        assertThat(spawnPoint.getId()).isEqualTo(ELEMENT_ID);
        assertThat(spawnPoint.getLocation()).isEqualTo(location);
        assertThat(spawnPoint.getTeamId()).isEqualTo(TEAM_ID);
    }
}
