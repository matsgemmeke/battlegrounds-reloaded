package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
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

    private static final Location INITIATION_LOCATION = new Location(null, 1, 1, 1, 1.0f, 1.0f);
    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();

    @Mock
    private DamageSource damageSource;
    @Mock
    private ItemEffectSource effectSource;
    @Mock
    private SpawnPointRegistry spawnPointRegistry;
    @Mock
    private TriggerTarget triggerTarget;
    @InjectMocks
    private MarkSpawnPointEffectPerformance performance;

    @Test
    void isPerformingReturnsFalseEvenAfterStartingPerformance() {
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        performance.perform(context);
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void performCreatesNewCustomSpawnPointAndAssignsToDeployer() {
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);

        performance.perform(context);

        verify(spawnPointRegistry).setCustomSpawnPoint(eq(DAMAGE_SOURCE_ID), any(SpawnPoint.class));
    }

    @Test
    void rollbackDoesNotResetSpawnPointWhenNotHavingPerformed() {
        performance.rollback();

        verifyNoInteractions(spawnPointRegistry);
    }

    @Test
    void rollbackResetsSpawnPointWhenHavingPerformed() {
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);

        performance.perform(context);
        performance.rollback();

        verify(spawnPointRegistry).setCustomSpawnPoint(DAMAGE_SOURCE_ID, null);
    }
}
