package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn;

import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.configuration.serialization.LocationDataSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpawnPointDataReaderTest {

    private static final int ELEMENT_ID = 1;
    private static final String ELEMENT_TYPE = "SPAWN_POINT";
    private static final LocationData LOCATION_DATA = new LocationData("world", 1.1, 2.2, 3.3, 90.0f, 90.0f);
    private static final int TEAM_ID = 2;

    @Mock
    private LocationDataSerializer locationDataSerializer;
    @Mock
    private Section section;
    @InjectMocks
    private SpawnPointDataReader spawnPointDataReader;

    @Test
    @DisplayName("read returns SpawnPointData with null values when given section does not contain them")
    void read_unknownValues() {
        when(section.getInt("element-id")).thenReturn(Optional.empty());
        when(section.getString("element-type")).thenReturn(Optional.empty());
        when(section.getSection("location")).thenReturn(Optional.empty());
        when(section.getInt("team-id")).thenReturn(Optional.empty());

        ElementData elementData = spawnPointDataReader.read(section);

        assertThat(elementData).isInstanceOfSatisfying(SpawnPointData.class, spawnPointData -> {
            assertThat(spawnPointData.getElementId()).isNull();
            assertThat(spawnPointData.getElementType()).isNull();
            assertThat(spawnPointData.getLocationData()).isNull();
            assertThat(spawnPointData.getTeamId()).isNull();
        });
    }

    @Test
    @DisplayName("read returns SpawnPointData with values from given section")
    void read_successful() {
        Section locationSection = mock(Section.class);

        when(section.getInt("element-id")).thenReturn(Optional.of(ELEMENT_ID));
        when(section.getString("element-type")).thenReturn(Optional.of(ELEMENT_TYPE));
        when(section.getSection("location")).thenReturn(Optional.of(locationSection));
        when(locationDataSerializer.deserialize(locationSection)).thenReturn(LOCATION_DATA);
        when(section.getInt("team-id")).thenReturn(Optional.of(TEAM_ID));

        ElementData elementData = spawnPointDataReader.read(section);

        assertThat(elementData).isInstanceOfSatisfying(SpawnPointData.class, spawnPointData -> {
            assertThat(spawnPointData.getElementId()).isEqualTo(ELEMENT_ID);
            assertThat(spawnPointData.getElementType()).isEqualTo(ELEMENT_TYPE);
            assertThat(spawnPointData.getLocationData()).isEqualTo(LOCATION_DATA);
            assertThat(spawnPointData.getTeamId()).isEqualTo(TEAM_ID);
        });
    }
}
