package nl.matsgemmeke.battlegrounds.game.openmode.component.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OpenModeRespawnHandlerTest {

    private static final UUID ENTITY_ID = UUID.randomUUID();

    private SpawnPointRegistry spawnPointRegistry;

    @BeforeEach
    public void setUp() {
        spawnPointRegistry = mock(SpawnPointRegistry.class);
    }

    @Test
    public void consumeRespawnLocationReturnsEmptyOptionalWhenGivenEntityIdHasNoCustomSpawnPoint() {
        when(spawnPointRegistry.getCustomSpawnPoint(ENTITY_ID)).thenReturn(Optional.empty());

        OpenModeRespawnHandler respawnHandler = new OpenModeRespawnHandler(spawnPointRegistry);
        Optional<Location> locationOptional = respawnHandler.consumeRespawnLocation(ENTITY_ID);

        assertThat(locationOptional).isEmpty();
    }

    @Test
    public void consumeRespawnLocationResetsCustomSpawnPointAndReturnsOptionalWithLocation() {
        Location location = new Location(null, 1, 1, 1);

        SpawnPoint spawnPoint = mock(SpawnPoint.class);
        when(spawnPoint.getLocation()).thenReturn(location);

        when(spawnPointRegistry.getCustomSpawnPoint(ENTITY_ID)).thenReturn(Optional.of(spawnPoint));

        OpenModeRespawnHandler respawnHandler = new OpenModeRespawnHandler(spawnPointRegistry);
        Optional<Location> locationOptional = respawnHandler.consumeRespawnLocation(ENTITY_ID);

        assertThat(locationOptional).hasValue(location);

        verify(spawnPointRegistry).setCustomSpawnPoint(ENTITY_ID, null);
    }
}
