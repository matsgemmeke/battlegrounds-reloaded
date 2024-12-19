package nl.matsgemmeke.battlegrounds.game.spawn;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class SpawnPointStorageTest {

    @Test
    public void getCustomSpawnPointReturnsNullIfThereIsNoCustomSpawnPointSetForGivenEntity() {
        GameEntity gameEntity = mock(GameEntity.class);

        SpawnPointStorage storage = new SpawnPointStorage();
        SpawnPoint spawnPoint = storage.getCustomSpawnPoint(gameEntity);

        assertNull(spawnPoint);
    }

    @Test
    public void getCustomSpawnPointReturnsSpawnPointObjectAssignedToGivenEntity() {
        GameEntity gameEntity = mock(GameEntity.class);
        SpawnPoint spawnPoint = mock(SpawnPoint.class);

        SpawnPointStorage storage = new SpawnPointStorage();
        storage.setCustomSpawnPoint(gameEntity, spawnPoint);

        SpawnPoint result = storage.getCustomSpawnPoint(gameEntity);

        assertNotNull(result);
        assertEquals(spawnPoint, result);
    }
}
