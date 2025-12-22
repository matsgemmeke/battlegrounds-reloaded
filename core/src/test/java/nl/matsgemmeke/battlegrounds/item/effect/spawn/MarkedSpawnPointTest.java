package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.Removable;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkedSpawnPointTest {

    private static final float YAW = 1.0f;

    @Mock(extraInterfaces = Removable.class)
    private ItemEffectSource source;

    private MarkedSpawnPoint spawnPoint;

    @BeforeEach
    void setUp() {
        spawnPoint = new MarkedSpawnPoint(source, YAW);
    }

    @Test
    void getLocationReturnsEffectSourceLocationWithGivenYawRotation() {
        Location sourceLocation = new Location(null, 1, 1, 1);
        when(source.getLocation()).thenReturn(sourceLocation);

        Location spawnPointLocation = spawnPoint.getLocation();

        assertEquals(YAW, spawnPointLocation.getYaw());
        assertEquals(1, spawnPointLocation.getX());
        assertEquals(1, spawnPointLocation.getY());
        assertEquals(1, spawnPointLocation.getZ());
    }

    @Test
    void onSpawnRemovesEffectSourceWhenItExists() {
        when(source.exists()).thenReturn(true);

        spawnPoint.onSpawn();

        verify((Removable) source).remove();
    }

    @Test
    void onSpawnDoesNotRemoveEffectSourceWhenItDoesNotExist() {
        when(source.exists()).thenReturn(false);

        spawnPoint.onSpawn();

        verify((Removable) source, never()).remove();
    }
}
