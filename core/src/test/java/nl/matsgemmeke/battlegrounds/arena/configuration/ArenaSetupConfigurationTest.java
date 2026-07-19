package nl.matsgemmeke.battlegrounds.arena.configuration;

import nl.matsgemmeke.battlegrounds.arena.configuration.map.ArenaMapData;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.TestValidatorFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaSetupConfigurationTest {

    private static final String CREATED_AT_TEXT = "2026-05-30T18:00:00Z";
    private static final Instant CREATED_AT = Instant.parse(CREATED_AT_TEXT);
    private static final String CREATED_BY_TEXT = "2c11afe2-48f0-4399-9a04-195bb8ac640e";
    private static final UUID CREATED_BY = UUID.fromString(CREATED_BY_TEXT);

    private static final String MAP_NAME = "Level 1";
    private static final String MAP_CREATED_AT_TEXT_FUTURE = "2126-06-30T18:00:00Z";

    @Mock
    private ConfigurationFile configurationFile;
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
        when(configurationFile.getString("created-at")).thenReturn(Optional.empty());

        Optional<Instant> createdAtOptional = setupConfiguration.getCreatedAt();

        assertThat(createdAtOptional).isEmpty();
    }

    @Test
    @DisplayName("getCreatedAt returns optional with configuration file value as instant")
    void getCreatedAt_successful() {
        when(configurationFile.getString("created-at")).thenReturn(Optional.of(CREATED_AT_TEXT));

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
        when(configurationFile.getString("created-by")).thenReturn(Optional.empty());

        Optional<UUID> createdByOptional = setupConfiguration.getCreatedBy();

        assertThat(createdByOptional).isEmpty();
    }

    @Test
    @DisplayName("getCreatedBy returns optional with configuration file value as uuid")
    void getCreatedBy_successful() {
        when(configurationFile.getString("created-by")).thenReturn(Optional.of(CREATED_BY_TEXT));

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
    @DisplayName("getMaps returns empty list when maps section does not exist")
    void getMaps_mapsSectionNotExists() {
        when(configurationFile.getConfigurationSection("maps")).thenReturn(Optional.empty());

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).isEmpty();
    }

    @Test
    @DisplayName("getMaps returns empty list when maps section has no keys")
    void getMaps_emptyMapsSection() {
        ConfigurationSection mapsSection = mock(ConfigurationSection.class);
        when(mapsSection.getKeys(false)).thenReturn(Set.of());

        when(configurationFile.getConfigurationSection("maps")).thenReturn(Optional.of(mapsSection));

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).isEmpty();
    }

    @Test
    @DisplayName("getMaps returns empty list when a single saved map is invalid because of missing name")
    void getMaps_missingName() {
        ConfigurationSection mapsSection = mock(ConfigurationSection.class);
        when(mapsSection.getKeys(false)).thenReturn(Set.of("level-1"));

        when(configurationFile.getConfigurationSection("maps")).thenReturn(Optional.of(mapsSection));
        when(configurationFile.getString("maps.level-1.name")).thenReturn(Optional.empty());

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).isEmpty();

        verify(logger).severe("""
                Failed to load map level-1: Validation failed for ArenaMapData (1 constraint violation):
                 - name: value is required""");
    }

    @Test
    @DisplayName("getMaps returns empty list when a single saved map is invalid because of a future createdAt date")
    void getMaps_futureCreatedAt() {
        ConfigurationSection mapsSection = mock(ConfigurationSection.class);
        when(mapsSection.getKeys(false)).thenReturn(Set.of("level-1"));

        when(configurationFile.getConfigurationSection("maps")).thenReturn(Optional.of(mapsSection));
        when(configurationFile.getString("maps.level-1.name")).thenReturn(Optional.of(MAP_NAME));
        when(configurationFile.getString("maps.level-1.created-at")).thenReturn(Optional.of(MAP_CREATED_AT_TEXT_FUTURE));

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).isEmpty();

        verify(logger).severe("""
                Failed to load map level-1: Validation failed for ArenaMapData (1 constraint violation):
                 - created-at: map creation date must be in the past""");
    }

    @ParameterizedTest
    @CsvSource(value = {
            "2026-06-30T18:00:00Z,2026-06-30T18:00:00Z,2c11afe2-48f0-4399-9a04-195bb8ac640e,2c11afe2-48f0-4399-9a04-195bb8ac640e",
            "invalid,null,2c11afe2-48f0-4399-9a04-195bb8ac640e,2c11afe2-48f0-4399-9a04-195bb8ac640e",
            "2026-06-30T18:00:00Z,2026-06-30T18:00:00Z,invalid,null"
    }, nullValues = "null")
    @DisplayName("getMaps returns list with valid map data")
    void getMaps_successful(String createdAt, Instant expectedCreatedAt, String createdBy, UUID expectedCreatedBy) {
        ConfigurationSection mapsSection = mock(ConfigurationSection.class);
        when(mapsSection.getKeys(false)).thenReturn(Set.of("level-1"));

        when(configurationFile.getConfigurationSection("maps")).thenReturn(Optional.of(mapsSection));
        when(configurationFile.getString("maps.level-1.name")).thenReturn(Optional.of(MAP_NAME));
        when(configurationFile.getString("maps.level-1.created-at")).thenReturn(Optional.of(createdAt));
        when(configurationFile.getString("maps.level-1.created-by")).thenReturn(Optional.of(createdBy));

        Collection<ArenaMapData> maps = setupConfiguration.getMaps();

        assertThat(maps).satisfiesExactly(mapData -> {
            assertThat(mapData.name()).isEqualTo(MAP_NAME);
            assertThat(mapData.createdAt()).isEqualTo(expectedCreatedAt);
            assertThat(mapData.createdBy()).isEqualTo(expectedCreatedBy);
        });

        verifyNoInteractions(logger);
    }
}
