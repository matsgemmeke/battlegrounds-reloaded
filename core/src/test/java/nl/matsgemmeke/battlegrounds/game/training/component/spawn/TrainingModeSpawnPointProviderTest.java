package nl.matsgemmeke.battlegrounds.game.training.component.spawn;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainingModeSpawnPointProviderTest {

    private SpawnPointStorage spawnPointStorage;

    @BeforeEach
    public void setUp() {
        spawnPointStorage = new SpawnPointStorage();
    }

    @Test
    public void hasSpawnPointReturnsTrueIfGivenEntityHasCustomSpawnPoint() {
        GameEntity gameEntity = mock(GameEntity.class);
        SpawnPoint spawnPoint = mock(SpawnPoint.class);

        spawnPointStorage.setCustomSpawnPoint(gameEntity, spawnPoint);

        TrainingModeSpawnPointProvider spawnPointProvider = new TrainingModeSpawnPointProvider(spawnPointStorage);
        boolean hasSpawnPoint = spawnPointProvider.hasSpawnPoint(gameEntity);

        assertTrue(hasSpawnPoint);
    }

    @Test
    public void hasSpawnPointReturnsFalseIfGivenEntityDoesNotHaveCustomSpawnPoint() {
        GameEntity gameEntity = mock(GameEntity.class);

        TrainingModeSpawnPointProvider spawnPointProvider = new TrainingModeSpawnPointProvider(spawnPointStorage);
        boolean hasSpawnPoint = spawnPointProvider.hasSpawnPoint(gameEntity);

        assertFalse(hasSpawnPoint);
    }

    @Test
    public void respawnEntityThrowsExceptionIfGivenEntityHasNoSpawnPoint() {
        GameEntity gameEntity = mock(GameEntity.class);

        TrainingModeSpawnPointProvider spawnPointProvider = new TrainingModeSpawnPointProvider(spawnPointStorage);

        assertThrows(IllegalStateException.class, () -> spawnPointProvider.respawnEntity(gameEntity));
    }

    @Test
    public void respawnEntityResetsSpawnPointAndReturnsLocation() {
        GameEntity gameEntity = mock(GameEntity.class);
        Location spawnPointLocation = new Location(null, 1, 1, 1);

        SpawnPoint spawnPoint = mock(SpawnPoint.class);
        when(spawnPoint.getLocation()).thenReturn(spawnPointLocation);

        spawnPointStorage.setCustomSpawnPoint(gameEntity, spawnPoint);

        TrainingModeSpawnPointProvider spawnPointProvider = new TrainingModeSpawnPointProvider(spawnPointStorage);
        Location respawnLocation = spawnPointProvider.respawnEntity(gameEntity);

        assertEquals(spawnPointLocation, respawnLocation);
        assertFalse(spawnPointProvider.hasSpawnPoint(gameEntity));

        verify(spawnPoint).onSpawn(gameEntity);
    }

    @Test
    public void setCustomSpawnPointAssignsSpawnPointToGivenEntity() {
        GameEntity gameEntity = mock(GameEntity.class);
        SpawnPoint spawnPoint = mock(SpawnPoint.class);

        TrainingModeSpawnPointProvider spawnPointProvider = new TrainingModeSpawnPointProvider(spawnPointStorage);
        spawnPointProvider.setCustomSpawnPoint(gameEntity, spawnPoint);

        assertTrue(spawnPointProvider.hasSpawnPoint(gameEntity));
    }
}
