package nl.matsgemmeke.battlegrounds.item.trigger;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.*;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impl.EntityImpactTrigger;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriggerExecutorFactoryTest {

    private static final double RANGE = 2.5;
    private static final long DELAY = 10L;
    private static final long INTERVAL = 2L;

    @Mock
    private Provider<EnemyProximityTrigger> enemyProximityTriggerProvider;
    @Mock
    private Provider<EntityImpactTrigger> entityImpactTriggerProvider;
    @Mock
    private Scheduler scheduler;

    private TriggerExecutorFactory factory;

    @BeforeEach
    void setUp() {
        factory = new TriggerExecutorFactory(enemyProximityTriggerProvider, entityImpactTriggerProvider, scheduler);
    }

    @Test
    @DisplayName("create returns TriggerExecutor with BlockImpactTrigger instance")
    void create_blockImpactTrigger() {
        BlockImpactTriggerSpec spec = new BlockImpactTriggerSpec();
        spec.type = "BLOCK_IMPACT";
        spec.delay = DELAY;
        spec.interval = INTERVAL;

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isFalse();
    }

    @Test
    @DisplayName("create returns TriggerExecutor with EnemyProximityTrigger instance")
    void create_enemyProximityTrigger() {
        EnemyProximityTriggerSpec spec = new EnemyProximityTriggerSpec();
        spec.type = "ENEMY_PROXIMITY";
        spec.delay = DELAY;
        spec.interval = INTERVAL;
        spec.range = RANGE;
        spec.repeating = true;

        EnemyProximityTrigger trigger = mock(EnemyProximityTrigger.class);
        when(enemyProximityTriggerProvider.get()).thenReturn(trigger);

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isTrue();

        verify(trigger).setCheckingRange(RANGE);
    }

    @Test
    @DisplayName("create returns TriggerExecutor with EntityImpactTrigger instance")
    void create_entityImpactTrigger() {
        EntityImpactTriggerSpec spec = new EntityImpactTriggerSpec();
        spec.type = "ENTITY_IMPACT";
        spec.delay = DELAY;
        spec.interval = INTERVAL;

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isFalse();
    }

    @Test
    @DisplayName("create returns TriggerExecutor with FloorHitTrigger instance")
    void create_floorHitTrigger() {
        FloorHitTriggerSpec spec = new FloorHitTriggerSpec();
        spec.type = "FLOOR_HIT";
        spec.delay = DELAY;
        spec.interval = INTERVAL;

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isFalse();
    }

    @Test
    @DisplayName("create returns TriggerExecutor with ScheduledTrigger instance that uses a single run schedule")
    void create_scheduledTriggerWithSingleRunSchedule() {
        ScheduledTriggerSpec spec = new ScheduledTriggerSpec();
        spec.type = "SCHEDULED";
        spec.offsetDelays = List.of(DELAY);

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isFalse();
    }

    @Test
    @DisplayName("create returns TriggerExecutor with ScheduledTrigger instance that uses a sequence schedule")
    void create_scheduledTriggerWithSequenceSchedule() {
        ScheduledTriggerSpec spec = new ScheduledTriggerSpec();
        spec.type = "SCHEDULED";
        spec.offsetDelays = List.of(10L, 20L);

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isFalse();
    }
}
