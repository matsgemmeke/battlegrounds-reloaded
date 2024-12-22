package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
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
        Location eyeLocation = new Location(null, 1, 1, 1, 1.0f, 1.0f);

        LivingEntity entity = mock(LivingEntity.class);
        when(entity.getEyeLocation()).thenReturn(eyeLocation);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(entity);

        EffectSource source = mock(EffectSource.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(spawnPointProvider);
        effect.activate(context);

        ArgumentCaptor<MarkedSpawnPoint> spawnPointCaptor = ArgumentCaptor.forClass(MarkedSpawnPoint.class);
        verify(spawnPointProvider).setCustomSpawnPoint(eq(holder), spawnPointCaptor.capture());
    }
}
