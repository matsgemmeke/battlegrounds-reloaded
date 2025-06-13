package nl.matsgemmeke.battlegrounds.game.spawn;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class SpawnPointContainerTest {

    @Test
    public void getCustomSpawnPointReturnsNullIfThereIsNoCustomSpawnPointSetForGivenEntityId() {
        UUID entityId = UUID.randomUUID();

        SpawnPointContainer container = new SpawnPointContainer();
        SpawnPoint spawnPoint = container.getCustomSpawnPoint(entityId);

        assertNull(spawnPoint);
    }

    @Test
    public void getCustomSpawnPointReturnsSpawnPointObjectAssignedToGivenEntityId() {
        SpawnPoint spawnPoint = mock(SpawnPoint.class);
        UUID entityId = UUID.randomUUID();

        SpawnPointContainer container = new SpawnPointContainer();
        container.setCustomSpawnPoint(entityId, spawnPoint);

        SpawnPoint result = container.getCustomSpawnPoint(entityId);

        assertNotNull(result);
        assertEquals(spawnPoint, result);
    }
}
