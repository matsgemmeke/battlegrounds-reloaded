package nl.matsgemmeke.battlegrounds.game.component.spawn;

import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DefaultSpawnPointRegistryTest {

    private static final UUID ENTITY_ID = UUID.randomUUID();

    private DefaultSpawnPointRegistry spawnPointRegistry;

    @BeforeEach
    void setUp() {
        spawnPointRegistry = new DefaultSpawnPointRegistry();
    }

    @Test
    @DisplayName("getCustomSpawnPoint returns empty optional when none is set for given entity id")
    void getCustomSpawnPoint_noneSet() {
        Optional<SpawnPoint> spawnPointOptional = spawnPointRegistry.getCustomSpawnPoint(ENTITY_ID);

        assertThat(spawnPointOptional).isEmpty();
    }

    @Test
    @DisplayName("getCustomSpawnPoint returns optional with corresponding custom spawn point for given entity id")
    void getCustomSpawnPoint_successful() {
        SpawnPoint spawnPoint = mock(SpawnPoint.class);

        spawnPointRegistry.setCustomSpawnPoint(ENTITY_ID, spawnPoint);
        Optional<SpawnPoint> spawnPointOptional = spawnPointRegistry.getCustomSpawnPoint(ENTITY_ID);

        assertThat(spawnPointOptional).hasValue(spawnPoint);
    }
}
