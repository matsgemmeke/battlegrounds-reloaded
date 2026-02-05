package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
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
    private Actor actor;

    private MarkedSpawnPoint spawnPoint;

    @BeforeEach
    void setUp() {
        spawnPoint = new MarkedSpawnPoint(actor, YAW);
    }

    @Test
    void getLocationReturnsEffectSourceLocationWithGivenYawRotation() {
        Location actorLocation = new Location(null, 1, 1, 1);
        when(actor.getLocation()).thenReturn(actorLocation);

        Location spawnPointLocation = spawnPoint.getLocation();

        assertEquals(YAW, spawnPointLocation.getYaw());
        assertEquals(1, spawnPointLocation.getX());
        assertEquals(1, spawnPointLocation.getY());
        assertEquals(1, spawnPointLocation.getZ());
    }

    @Test
    void onSpawnRemovesEffectSourceWhenItExists() {
        when(actor.exists()).thenReturn(true);

        spawnPoint.onSpawn();

        verify((Removable) actor).remove();
    }

    @Test
    void onSpawnDoesNotRemoveEffectSourceWhenItDoesNotExist() {
        when(actor.exists()).thenReturn(false);

        spawnPoint.onSpawn();

        verify((Removable) actor, never()).remove();
    }
}
