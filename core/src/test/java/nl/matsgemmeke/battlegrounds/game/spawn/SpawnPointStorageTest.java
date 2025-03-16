package nl.matsgemmeke.battlegrounds.game.spawn;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class SpawnPointStorageTest {

    @Test
    public void getCustomSpawnPointReturnsNullIfThereIsNoCustomSpawnPointSetForGivenEntityId() {
        UUID entityId = UUID.randomUUID();

        SpawnPointStorage storage = new SpawnPointStorage();
        SpawnPoint spawnPoint = storage.getCustomSpawnPoint(entityId);

        assertNull(spawnPoint);
    }

    @Test
    public void getCustomSpawnPointReturnsSpawnPointObjectAssignedToGivenEntityId() {
        SpawnPoint spawnPoint = mock(SpawnPoint.class);
        UUID entityId = UUID.randomUUID();

        SpawnPointStorage storage = new SpawnPointStorage();
        storage.setCustomSpawnPoint(entityId, spawnPoint);

        SpawnPoint result = storage.getCustomSpawnPoint(entityId);

        assertNotNull(result);
        assertEquals(spawnPoint, result);
    }
}
