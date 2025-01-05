package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MarkedSpawnPointTest {

    private static final float YAW = 1.0f;

    private ItemEffectSource source;

    @BeforeEach
    public void setUp() {
        source = mock(ItemEffectSource.class);
    }

    @Test
    public void getLocationReturnsEffectSourceLocationWithGivenYawRotation() {
        Location sourceLocation = new Location(null, 1, 1, 1);
        when(source.getLocation()).thenReturn(sourceLocation);

        MarkedSpawnPoint spawnPoint = new MarkedSpawnPoint(source, YAW);
        Location spawnPointLocation = spawnPoint.getLocation();

        assertEquals(YAW, spawnPointLocation.getYaw());
        assertEquals(1, spawnPointLocation.getX());
        assertEquals(1, spawnPointLocation.getY());
        assertEquals(1, spawnPointLocation.getZ());
    }

    @Test
    public void onSpawnRemovesEffectSourceIfItExists() {
        GameEntity gameEntity = mock(GameEntity.class);

        when(source.exists()).thenReturn(true);

        MarkedSpawnPoint spawnPoint = new MarkedSpawnPoint(source, YAW);
        spawnPoint.onSpawn(gameEntity);

        verify(source).remove();
    }

    @Test
    public void onSpawnDoesNotRemoveEffectSourceIfItDoesNotExist() {
        GameEntity gameEntity = mock(GameEntity.class);

        when(source.exists()).thenReturn(false);

        MarkedSpawnPoint spawnPoint = new MarkedSpawnPoint(source, YAW);
        spawnPoint.onSpawn(gameEntity);

        verify(source, never()).remove();
    }
}
