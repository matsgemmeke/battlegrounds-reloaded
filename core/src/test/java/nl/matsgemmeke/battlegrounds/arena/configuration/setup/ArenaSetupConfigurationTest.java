package nl.matsgemmeke.battlegrounds.arena.configuration.setup;

import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementDataFactory;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementType;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn.SpawnPointData;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.map.ArenaMapData;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.spawn.CreateSpawnPointData;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.TestValidatorFactory;
import nl.matsgemmeke.battlegrounds.validation.ValidationException;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaSetupConfigurationTest {

    private static final String CREATED_AT_TEXT = "2026-05-30T18:00:00Z";
    private static final Instant CREATED_AT = Instant.parse(CREATED_AT_TEXT);
    private static final String CREATED_BY_TEXT = "2c11afe2-48f0-4399-9a04-195bb8ac640e";
    private static final UUID CREATED_BY = UUID.fromString(CREATED_BY_TEXT);

    private static final String MAP_KEY = "level-1";
    private static final String MAP_NAME = "Level 1";
    private static final String MAP_CREATED_AT_TEXT_FUTURE = "2126-06-30T18:00:00Z";

    private static final String SPAWN_POINT_SECTION_PATH = "maps.level-1.elements.1";
    private static final int SPAWN_POINT_ELEMENT_ID = 1;
    private static final String SPAWN_POINT_ELEMENT_TYPE = "SPAWN_POINT";
    private static final int SPAWN_POINT_TEAM_ID = 2;
    private static final String SPAWN_POINT_LOCATION_WORLD = "world";
    private static final double SPAWN_POINT_LOCATION_X = 1.1;
    private static final double SPAWN_POINT_LOCATION_Y = 2.2;
    private static final double SPAWN_POINT_LOCATION_Z = 3.3;
    private static final float SPAWN_POINT_LOCATION_YAW = 180.0f;
    private static final float SPAWN_POINT_LOCATION_PITCH = 90.0f;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationFile configurationFile;
    @Mock
    private ElementDataFactory elementDataFactory;
    @Mock
    private Logger logger;
    @Spy
    private ObjectValidator objectValidator = TestValidatorFactory.createObjectValidator();
    @InjectMocks
    private ArenaSetupConfiguration setupConfiguration;

    @Test
    @DisplayName("save saves the configuration file")
    void save() {
        setupConfiguration.save();

        verify(configurationFile).save();
    }

    @Test
    @DisplayName("getCreatedAt returns empty optional when configuration file does not have a value")
    void getCreatedAt_valueNotFound() {
        when(configurationFile.getRootSection().getString("created-at")).thenReturn(Optional.empty());

        Optional<Instant> createdAtOptional = setupConfiguration.getCreatedAt();

        assertThat(createdAtOptional).isEmpty();
    }

    @Test
    @DisplayName("getCreatedAt returns optional with configuration file value as instant")
    void getCreatedAt_successful() {
        when(configurationFile.getRootSection().getString("created-at")).thenReturn(Optional.of(CREATED_AT_TEXT));

        Optional<Instant> createdAtOptional = setupConfiguration.getCreatedAt();

        assertThat(createdAtOptional).hasValue(CREATED_AT);
    }

    @Test
    @DisplayName("setCreatedAt sets given instant as string in configuration file")
    void setCreatedAt() {
        setupConfiguration.setCreatedAt(CREATED_AT);

        verify(configurationFile).set("created-at", CREATED_AT_TEXT);
    }

    @Test
    @DisplayName("getCreatedBy returns empty optional when configuration file does not have a value")
    void getCreatedBy_valueNotFound() {
        when(configurationFile.getRootSection().getString("created-by")).thenReturn(Optional.empty());

        Optional<UUID> createdByOptional = setupConfiguration.getCreatedBy();

        assertThat(createdByOptional).isEmpty();
    }

    @Test
    @DisplayName("getCreatedBy returns optional with configuration file value as uuid")
    void getCreatedBy_successful() {
        when(configurationFile.getRootSection().getString("created-by")).thenReturn(Optional.of(CREATED_BY_TEXT));

        Optional<UUID> createdByOptional = setupConfiguration.getCreatedBy();

        assertThat(createdByOptional).hasValue(CREATED_BY);
    }

    @Test
    @DisplayName("setCreatedBy sets given uuid as string in configuration file")
    void setCreatedBy() {
        setupConfiguration.setCreatedBy(CREATED_BY);

        verify(configurationFile).set("created-by", CREATED_BY_TEXT);
    }

    @Test
    @DisplayName("createMap creates basic map info in configuration file")
    void createMap() {
        MapCreationInfo mapCreationInfo = new MapCreationInfo(MAP_NAME, CREATED_AT, CREATED_BY);

        setupConfiguration.createMap(mapCreationInfo);

        verify(configurationFile).set("maps.level-1.name", MAP_NAME);
        verify(configurationFile).set("maps.level-1.created-at", CREATED_AT_TEXT);
        verify(configurationFile).set("maps.level-1.created-by", CREATED_BY_TEXT);
    }

    @Test
    @DisplayName("removeMap removes map section from configuration file")
    void removeMap() {
        setupConfiguration.removeMap(MAP_NAME);

        verify(configurationFile.getRootSection()).removeSection("maps.level-1");
        verify(configurationFile).save();
    }

    @Test
    @DisplayName("getMaps returns empty list when maps section does not exist")
    void getMaps_mapsSectionNotExists() {
        when(configurationFile.getRootSection().getSection("maps")).thenReturn(Optional.empty());

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).isEmpty();
    }

    @Test
    @DisplayName("getMaps returns empty list when maps section has no keys")
    void getMaps_emptyMapsSection() {
        Section mapsSection = mock(Section.class);
        when(mapsSection.getKeys()).thenReturn(Set.of());

        when(configurationFile.getRootSection().getSection("maps")).thenReturn(Optional.of(mapsSection));

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).isEmpty();
    }

    @Test
    @DisplayName("getMaps returns empty list when a single saved map is invalid because of missing name")
    void getMaps_missingName() {
        Section mapsSection = mock(Section.class);
        when(mapsSection.getKeys()).thenReturn(Set.of(MAP_KEY));
        when(mapsSection.getString("level-1.name")).thenReturn(Optional.empty());

        when(configurationFile.getRootSection().getSection("maps")).thenReturn(Optional.of(mapsSection));

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).isEmpty();

        verify(logger).severe("""
                Failed to load map level-1: Validation failed for object ArenaMapData (1 constraint violation)
                 - name: name is required""");
    }

    @Test
    @DisplayName("getMaps returns empty list when a single saved map is invalid because of a future createdAt date")
    void getMaps_futureCreatedAt() {
        Section mapsSection = mock(Section.class);
        when(mapsSection.getKeys()).thenReturn(Set.of(MAP_KEY));
        when(mapsSection.getString("level-1.name")).thenReturn(Optional.of(MAP_NAME));
        when(mapsSection.getString("level-1.created-at")).thenReturn(Optional.of(MAP_CREATED_AT_TEXT_FUTURE));

        when(configurationFile.getRootSection().getSection("maps")).thenReturn(Optional.of(mapsSection));

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).isEmpty();

        verify(logger).severe("""
                Failed to load map level-1: Validation failed for object ArenaMapData (1 constraint violation)
                 - createdAt: map creation date must be in the past""");
    }

    @Test
    @DisplayName("getMaps returns list with map data without elements")
    void getMaps_withoutElements() {
        Section mapsSection = mock(Section.class);
        when(mapsSection.getKeys()).thenReturn(Set.of(MAP_KEY));
        when(mapsSection.getString("level-1.name")).thenReturn(Optional.of(MAP_NAME));
        when(mapsSection.getString("level-1.created-at")).thenReturn(Optional.of(CREATED_AT_TEXT));
        when(mapsSection.getString("level-1.created-by")).thenReturn(Optional.of(CREATED_BY_TEXT));
        when(mapsSection.getSection("level-1.elements")).thenReturn(Optional.empty());

        when(configurationFile.getRootSection().getSection("maps")).thenReturn(Optional.of(mapsSection));

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).satisfiesExactly(mapData -> {
            assertThat(mapData.name()).isEqualTo(MAP_NAME);
            assertThat(mapData.createdAt()).isEqualTo(CREATED_AT);
            assertThat(mapData.createdBy()).isEqualTo(CREATED_BY);
            assertThat(mapData.elements()).isEmpty();
        });
    }

    @Test
    @DisplayName("getMaps returns list with map data without elements whose section cannot be found")
    void getMaps_elementSectionNotFound() {
        Section elementsSection = mock(Section.class);
        when(elementsSection.getKeys()).thenReturn(Set.of(String.valueOf(SPAWN_POINT_ELEMENT_ID)));
        when(elementsSection.getSection(String.valueOf(SPAWN_POINT_ELEMENT_ID))).thenReturn(Optional.empty());

        Section mapsSection = mock(Section.class);
        when(mapsSection.getKeys()).thenReturn(Set.of(MAP_KEY));
        when(mapsSection.getString("level-1.name")).thenReturn(Optional.of(MAP_NAME));
        when(mapsSection.getString("level-1.created-at")).thenReturn(Optional.of(CREATED_AT_TEXT));
        when(mapsSection.getString("level-1.created-by")).thenReturn(Optional.of(CREATED_BY_TEXT));
        when(mapsSection.getSection("level-1.elements")).thenReturn(Optional.of(elementsSection));

        when(configurationFile.getRootSection().getSection("maps")).thenReturn(Optional.of(mapsSection));

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).satisfiesExactly(mapData -> {
            assertThat(mapData.name()).isEqualTo(MAP_NAME);
            assertThat(mapData.createdAt()).isEqualTo(CREATED_AT);
            assertThat(mapData.createdBy()).isEqualTo(CREATED_BY);
            assertThat(mapData.elements()).isEmpty();
        });
    }

    @Test
    @DisplayName("getMaps returns list with map data without elements whose section cannot be found")
    void getMaps_invalidElementType() {
        Section elementSection = mock(Section.class);
        when(elementSection.getString("element-type")).thenReturn(Optional.of("unknown-element"));

        Section elementsSection = mock(Section.class);
        when(elementsSection.getKeys()).thenReturn(Set.of(String.valueOf(SPAWN_POINT_ELEMENT_ID)));
        when(elementsSection.getSection(String.valueOf(SPAWN_POINT_ELEMENT_ID))).thenReturn(Optional.of(elementSection));

        Section mapsSection = mock(Section.class);
        when(mapsSection.getKeys()).thenReturn(Set.of(MAP_KEY));
        when(mapsSection.getString("level-1.name")).thenReturn(Optional.of(MAP_NAME));
        when(mapsSection.getString("level-1.created-at")).thenReturn(Optional.of(CREATED_AT_TEXT));
        when(mapsSection.getString("level-1.created-by")).thenReturn(Optional.of(CREATED_BY_TEXT));
        when(mapsSection.getSection("level-1.elements")).thenReturn(Optional.of(elementsSection));

        when(configurationFile.getRootSection().getSection("maps")).thenReturn(Optional.of(mapsSection));

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).satisfiesExactly(mapData -> {
            assertThat(mapData.name()).isEqualTo(MAP_NAME);
            assertThat(mapData.createdAt()).isEqualTo(CREATED_AT);
            assertThat(mapData.createdBy()).isEqualTo(CREATED_BY);
            assertThat(mapData.elements()).isEmpty();
        });
    }

    @Test
    @DisplayName("getMaps returns list with map data without elements whose section cannot be found")
    void getMaps_elementWithViolations() {
        SpawnPointData spawnPointData = new SpawnPointData();
        spawnPointData.setElementId(SPAWN_POINT_ELEMENT_ID);
        spawnPointData.setElementType(SPAWN_POINT_ELEMENT_TYPE);
        spawnPointData.setLocationData(new LocationData(null, 0, 0, 0, 0, 0));
        spawnPointData.setTeamId(0);

        Section elementSection = mock(Section.class);
        when(elementSection.getString("element-type")).thenReturn(Optional.of("SPAWN_POINT"));
        when(elementSection.getAbsolutePath()).thenReturn(SPAWN_POINT_SECTION_PATH);

        Section elementsSection = mock(Section.class);
        when(elementsSection.getKeys()).thenReturn(Set.of(String.valueOf(SPAWN_POINT_ELEMENT_ID)));
        when(elementsSection.getSection(String.valueOf(SPAWN_POINT_ELEMENT_ID))).thenReturn(Optional.of(elementSection));

        Section mapsSection = mock(Section.class);
        when(mapsSection.getKeys()).thenReturn(Set.of(MAP_KEY));
        when(mapsSection.getString("level-1.name")).thenReturn(Optional.of(MAP_NAME));
        when(mapsSection.getString("level-1.created-at")).thenReturn(Optional.of(CREATED_AT_TEXT));
        when(mapsSection.getString("level-1.created-by")).thenReturn(Optional.of(CREATED_BY_TEXT));
        when(mapsSection.getSection("level-1.elements")).thenReturn(Optional.of(elementsSection));

        when(configurationFile.getRootSection().getSection("maps")).thenReturn(Optional.of(mapsSection));
        when(elementDataFactory.create(ElementType.SPAWN_POINT, elementSection)).thenReturn(spawnPointData);

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).satisfiesExactly(mapData -> {
            assertThat(mapData.name()).isEqualTo(MAP_NAME);
            assertThat(mapData.createdAt()).isEqualTo(CREATED_AT);
            assertThat(mapData.createdBy()).isEqualTo(CREATED_BY);
            assertThat(mapData.elements()).isEmpty();
        });

        verify(logger).severe("""
                Failed to load element located at 'maps.level-1.elements.1': Validation failed for object SpawnPointData (2 constraint violations)
                 - teamId: team id must be greater than zero
                 - locationData.world: locations in configurations must have a defined world
                """.trim());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "2026-06-30T18:00:00Z,2026-06-30T18:00:00Z,2c11afe2-48f0-4399-9a04-195bb8ac640e,2c11afe2-48f0-4399-9a04-195bb8ac640e",
            "invalid,null,2c11afe2-48f0-4399-9a04-195bb8ac640e,2c11afe2-48f0-4399-9a04-195bb8ac640e",
            "2026-06-30T18:00:00Z,2026-06-30T18:00:00Z,invalid,null"
    }, nullValues = "null")
    @DisplayName("getMaps returns list with valid map data")
    void getMaps_successful(String createdAt, Instant expectedCreatedAt, String createdBy, UUID expectedCreatedBy) {
        SpawnPointData spawnPointData = new SpawnPointData();
        spawnPointData.setElementId(SPAWN_POINT_ELEMENT_ID);
        spawnPointData.setElementType(SPAWN_POINT_ELEMENT_TYPE);
        spawnPointData.setLocationData(new LocationData(SPAWN_POINT_LOCATION_WORLD, SPAWN_POINT_LOCATION_X, SPAWN_POINT_LOCATION_Y, SPAWN_POINT_LOCATION_Z, SPAWN_POINT_LOCATION_YAW, SPAWN_POINT_LOCATION_PITCH));
        spawnPointData.setTeamId(SPAWN_POINT_TEAM_ID);

        Section elementSection = mock(Section.class);
        when(elementSection.getString("element-type")).thenReturn(Optional.of("SPAWN_POINT"));

        Section elementsSection = mock(Section.class);
        when(elementsSection.getKeys()).thenReturn(Set.of(String.valueOf(SPAWN_POINT_ELEMENT_ID)));
        when(elementsSection.getSection(String.valueOf(SPAWN_POINT_ELEMENT_ID))).thenReturn(Optional.of(elementSection));

        Section mapsSection = mock(Section.class);
        when(mapsSection.getKeys()).thenReturn(Set.of(MAP_KEY));
        when(mapsSection.getString("level-1.name")).thenReturn(Optional.of(MAP_NAME));
        when(mapsSection.getString("level-1.created-at")).thenReturn(Optional.of(createdAt));
        when(mapsSection.getString("level-1.created-by")).thenReturn(Optional.of(createdBy));
        when(mapsSection.getSection("level-1.elements")).thenReturn(Optional.of(elementsSection));

        when(configurationFile.getRootSection().getSection("maps")).thenReturn(Optional.of(mapsSection));
        when(elementDataFactory.create(ElementType.SPAWN_POINT, elementSection)).thenReturn(spawnPointData);

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).satisfiesExactly(mapData -> {
            assertThat(mapData.name()).isEqualTo(MAP_NAME);
            assertThat(mapData.createdAt()).isEqualTo(expectedCreatedAt);
            assertThat(mapData.createdBy()).isEqualTo(expectedCreatedBy);
            assertThat(mapData.elements()).containsExactly(spawnPointData);
        });
    }

    @Test
    @DisplayName("createSpawnPoint throws IllegalArgumentException when given spawn point is invalid")
    void createSpawnPoint_invalid() {
        CreateSpawnPointData data = new CreateSpawnPointData(MAP_NAME, -1, null, -1);

        assertThatThrownBy(() -> setupConfiguration.createSpawnPoint(data))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create spawn point for invalid data")
                .cause()
                .isInstanceOf(ValidationException.class)
                .hasMessage("Validation failed for object CreateSpawnPointData (3 constraint violations)");
    }

    @Test
    @DisplayName("createSpawnPoint saves values of given data object to elements section of map")
    void createSpawnPoint_successful() {
        World world = mock(World.class);
        Location location = new Location(world, SPAWN_POINT_LOCATION_X, SPAWN_POINT_LOCATION_Y, SPAWN_POINT_LOCATION_Z);
        CreateSpawnPointData data = new CreateSpawnPointData(MAP_NAME, SPAWN_POINT_ELEMENT_ID, location, SPAWN_POINT_TEAM_ID);

        setupConfiguration.createSpawnPoint(data);

        verify(configurationFile).set("maps.level-1.elements.1.type", "SPAWN_POINT");
        verify(configurationFile).setLocation("maps.level-1.elements.1.location", location);
        verify(configurationFile).set("maps.level-1.elements.1.team-id", SPAWN_POINT_TEAM_ID);
        verify(configurationFile).save();
    }
}
