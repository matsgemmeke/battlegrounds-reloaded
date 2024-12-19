package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class MarkSpawnPointEffectTest {

    private SpawnPointProvider spawnPointProvider;

    @BeforeEach
    public void setUp() {
        spawnPointProvider = mock(SpawnPointProvider.class);
    }

    @Test
    public void activateCreatesNewCustomSpawnPointAndAssignsToHolder() {
        ItemHolder holder = mock(ItemHolder.class);
        Location sourceLocation = new Location(null, 1, 1, 1);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(spawnPointProvider);
        effect.activate(context);

        ArgumentCaptor<MarkedSpawnPoint> spawnPointCaptor = ArgumentCaptor.forClass(MarkedSpawnPoint.class);
        verify(spawnPointProvider).setCustomSpawnPoint(eq(holder), spawnPointCaptor.capture());
    }
}
