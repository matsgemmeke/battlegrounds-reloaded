package nl.matsgemmeke.battlegrounds.game.openmode.component.spawn;

import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OpenModeSpawnPointRegistryTest {

    @Test
    public void getCustomSpawnPointReturnsEmptyOptionalWhenNoneIsSetForGivenEntityId() {
        UUID entityId = UUID.randomUUID();

        OpenModeSpawnPointRegistry spawnPointRegistry = new OpenModeSpawnPointRegistry();
        Optional<SpawnPoint> spawnPointOptional = spawnPointRegistry.getCustomSpawnPoint(entityId);

        assertThat(spawnPointOptional).isEmpty();
    }

    @Test
    public void getCustomSpawnPointReturnsOptionalWithCorrespondingSpawnPointForGivenEntityId() {
        UUID entityId = UUID.randomUUID();
        SpawnPoint spawnPoint = mock(SpawnPoint.class);

        OpenModeSpawnPointRegistry spawnPointRegistry = new OpenModeSpawnPointRegistry();
        spawnPointRegistry.setCustomSpawnPoint(entityId, spawnPoint);
        Optional<SpawnPoint> spawnPointOptional = spawnPointRegistry.getCustomSpawnPoint(entityId);

        assertThat(spawnPointOptional).hasValue(spawnPoint);
    }

    @Test
    public void hasSpawnPointReturnsTrueIfGivenEntityIdHasCustomSpawnPoint() {
        SpawnPoint spawnPoint = mock(SpawnPoint.class);
        UUID entityId = UUID.randomUUID();

        OpenModeSpawnPointRegistry spawnPointRegistry = new OpenModeSpawnPointRegistry();
        spawnPointRegistry.setCustomSpawnPoint(entityId, spawnPoint);
        boolean hasSpawnPoint = spawnPointRegistry.hasSpawnPoint(entityId);

        assertTrue(hasSpawnPoint);
    }

    @Test
    public void hasSpawnPointReturnsFalseIfGivenEntityIdDoesNotHaveCustomSpawnPoint() {
        UUID entityId = UUID.randomUUID();

        OpenModeSpawnPointRegistry spawnPointRegistry = new OpenModeSpawnPointRegistry();
        boolean hasSpawnPoint = spawnPointRegistry.hasSpawnPoint(entityId);

        assertFalse(hasSpawnPoint);
    }
}
