package nl.matsgemmeke.battlegrounds.game.openmode.component.spawn;

import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointContainer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OpenModeSpawnPointProviderTest {

    private SpawnPointContainer spawnPointContainer;

    @BeforeEach
    public void setUp() {
        spawnPointContainer = new SpawnPointContainer();
    }

    @Test
    public void hasSpawnPointReturnsTrueIfGivenEntityIdHasCustomSpawnPoint() {
        SpawnPoint spawnPoint = mock(SpawnPoint.class);
        UUID entityId = UUID.randomUUID();

        spawnPointContainer.setCustomSpawnPoint(entityId, spawnPoint);

        OpenModeSpawnPointProvider spawnPointProvider = new OpenModeSpawnPointProvider(spawnPointContainer);
        boolean hasSpawnPoint = spawnPointProvider.hasSpawnPoint(entityId);

        assertTrue(hasSpawnPoint);
    }

    @Test
    public void hasSpawnPointReturnsFalseIfGivenEntityIdDoesNotHaveCustomSpawnPoint() {
        UUID entityId = UUID.randomUUID();

        OpenModeSpawnPointProvider spawnPointProvider = new OpenModeSpawnPointProvider(spawnPointContainer);
        boolean hasSpawnPoint = spawnPointProvider.hasSpawnPoint(entityId);

        assertFalse(hasSpawnPoint);
    }

    @Test
    public void respawnEntityThrowsExceptionIfGivenEntityHasNoSpawnPoint() {
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        OpenModeSpawnPointProvider spawnPointProvider = new OpenModeSpawnPointProvider(spawnPointContainer);

        assertThrows(IllegalStateException.class, () -> spawnPointProvider.respawnEntity(entity));
    }

    @Test
    public void respawnEntityResetsSpawnPointAndReturnsLocation() {
        Location spawnPointLocation = new Location(null, 1, 1, 1);
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        SpawnPoint spawnPoint = mock(SpawnPoint.class);
        when(spawnPoint.getLocation()).thenReturn(spawnPointLocation);

        spawnPointContainer.setCustomSpawnPoint(entityId, spawnPoint);

        OpenModeSpawnPointProvider spawnPointProvider = new OpenModeSpawnPointProvider(spawnPointContainer);
        Location respawnLocation = spawnPointProvider.respawnEntity(entity);

        assertEquals(spawnPointLocation, respawnLocation);
        assertFalse(spawnPointProvider.hasSpawnPoint(entityId));

        verify(spawnPoint).onSpawn(entity);
    }

    @Test
    public void setCustomSpawnPointAssignsSpawnPointToGivenEntityId() {
        SpawnPoint spawnPoint = mock(SpawnPoint.class);
        UUID entityId = UUID.randomUUID();

        OpenModeSpawnPointProvider spawnPointProvider = new OpenModeSpawnPointProvider(spawnPointContainer);
        spawnPointProvider.setCustomSpawnPoint(entityId, spawnPoint);

        assertTrue(spawnPointProvider.hasSpawnPoint(entityId));
    }
}
