package nl.matsgemmeke.battlegrounds.arena;

import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMapMetadata;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ArenaTest {

    private static final int ID = 1;
    private static final String MAP_NAME = "Level 1";
    private static final ArenaMapMetadata METADATA = new ArenaMapMetadata(Instant.parse("2026-01-01T12:00:00.00Z"), UUID.fromString("2c11afe2-48f0-4399-9a04-195bb8ac640e"));

    @Mock
    private ArenaSettings settings;

    private Arena arena;

    @BeforeEach
    void setUp() {
        arena = new Arena(ID, settings);
    }

    @Test
    void getSettings() {
        assertThat(arena.getSettings()).isEqualTo(settings);
    }

    @Test
    @DisplayName("addMap adds map to list")
    void addMap() {
        ArenaMap map = new ArenaMap(MAP_NAME, METADATA);

        arena.addMap(map);
        Optional<ArenaMap> mapOptional = arena.getMap(MAP_NAME);

        assertThat(mapOptional).hasValue(map);
    }

    @Test
    @DisplayName("removeMap removes map from list")
    void removeMap() {
        ArenaMap map = new ArenaMap(MAP_NAME, METADATA);

        arena.addMap(map);
        arena.removeMap(map);
        Optional<ArenaMap> mapOptional = arena.getMap(MAP_NAME);

        assertThat(mapOptional).isEmpty();
    }

    @Test
    @DisplayName("getMap returns empty optional when arena does not have a map by the given name")
    void getMap_unknownMap() {
        Optional<ArenaMap> mapOptional = arena.getMap(MAP_NAME);

        assertThat(mapOptional).isEmpty();
    }

    @Test
    @DisplayName("getMap returns optional with corresponding map")
    void getMap_successful() {
        ArenaMap map = new ArenaMap(MAP_NAME, METADATA);

        arena.addMap(map);
        Optional<ArenaMap> mapOptional = arena.getMap(MAP_NAME);

        assertThat(mapOptional).hasValue(map);
    }

    @Test
    @DisplayName("getMapNames returns list of all map names")
    void getMapNames() {
        ArenaMap map = new ArenaMap(MAP_NAME, METADATA);

        arena.addMap(map);
        List<String> mapNames = arena.getMapNames();

        assertThat(mapNames).containsExactly(MAP_NAME);
    }
}
