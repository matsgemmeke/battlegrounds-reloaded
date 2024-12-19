package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MarkedSpawnPointTest {

    private EffectSource source;

    @BeforeEach
    public void setUp() {
        source = mock(EffectSource.class);
    }

    @Test
    public void getLocationReturnsEffectSourceLocation() {
        Location sourceLocation = new Location(null, 1, 1, 1);
        when(source.getLocation()).thenReturn(sourceLocation);

        MarkedSpawnPoint spawnPoint = new MarkedSpawnPoint(source);
        Location spawnPointLocation = spawnPoint.getLocation();

        assertEquals(sourceLocation, spawnPointLocation);
    }

    @Test
    public void onSpawnRemovesEffectSourceIfItExists() {
        GameEntity gameEntity = mock(GameEntity.class);

        when(source.exists()).thenReturn(true);

        MarkedSpawnPoint spawnPoint = new MarkedSpawnPoint(source);
        spawnPoint.onSpawn(gameEntity);

        verify(source).remove();
    }

    @Test
    public void onSpawnDoesNotRemoveEffectSourceIfItDoesNotExist() {
        GameEntity gameEntity = mock(GameEntity.class);

        when(source.exists()).thenReturn(false);

        MarkedSpawnPoint spawnPoint = new MarkedSpawnPoint(source);
        spawnPoint.onSpawn(gameEntity);

        verify(source, never()).remove();
    }
}
