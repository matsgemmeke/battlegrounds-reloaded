package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkSpawnPointEffectPerformanceTest {

    private final static Location INITIATION_LOCATION = new Location(null, 1, 1, 1, 1.0f, 1.0f);

    @Mock
    private Entity entity;
    @Mock
    private ItemEffectSource source;
    @Mock
    private SpawnPointRegistry spawnPointRegistry;
    @InjectMocks
    private MarkSpawnPointEffectPerformance performance;

    @Test
    void isPerformingReturnsFalseEvenAfterStartingPerformance() {
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        performance.perform(context);
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void performCreatesNewCustomSpawnPointAndAssignsToDeployer() {
        UUID entityId = UUID.randomUUID();
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        when(entity.getUniqueId()).thenReturn(entityId);

        performance.perform(context);

        verify(spawnPointRegistry).setCustomSpawnPoint(eq(entityId), any(SpawnPoint.class));
    }

    @Test
    void rollbackDoesNotResetSpawnPointWhenNotHavingPerformed() {
        performance.rollback();

        verifyNoInteractions(spawnPointRegistry);
    }

    @Test
    void rollbackResetsSpawnPointWhenHavingPerformed() {
        UUID entityId = UUID.randomUUID();
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        when(entity.getUniqueId()).thenReturn(entityId);

        performance.perform(context);
        performance.rollback();

        verify(spawnPointRegistry).setCustomSpawnPoint(entityId, null);
    }
}
