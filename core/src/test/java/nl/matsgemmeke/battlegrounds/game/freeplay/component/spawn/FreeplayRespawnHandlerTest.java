package nl.matsgemmeke.battlegrounds.game.freeplay.component.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import org.bukkit.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FreeplayRespawnHandlerTest {

    private static final UUID ENTITY_ID = UUID.randomUUID();

    @Mock
    private SpawnPointRegistry spawnPointRegistry;
    @InjectMocks
    private FreeplayRespawnHandler respawnHandler;

    @Test
    @DisplayName("consumeRespawnLocation returns empty optional when given entity id has no custom spawn point")
    void consumeRespawnLocation_noCustomSpawnPointFound() {
        when(spawnPointRegistry.getCustomSpawnPoint(ENTITY_ID)).thenReturn(Optional.empty());

        Optional<Location> locationOptional = respawnHandler.consumeRespawnLocation(ENTITY_ID);

        assertThat(locationOptional).isEmpty();
    }

    @Test
    @DisplayName("consumeRespawnLocation resets custom spawn point and returns with its location")
    void consumeRespawnLocation_successful() {
        Location location = new Location(null, 1, 1, 1);

        SpawnPoint spawnPoint = mock(SpawnPoint.class);
        when(spawnPoint.getLocation()).thenReturn(location);

        when(spawnPointRegistry.getCustomSpawnPoint(ENTITY_ID)).thenReturn(Optional.of(spawnPoint));

        Optional<Location> locationOptional = respawnHandler.consumeRespawnLocation(ENTITY_ID);

        assertThat(locationOptional).hasValue(location);

        verify(spawnPointRegistry).setCustomSpawnPoint(ENTITY_ID, null);
    }
}
