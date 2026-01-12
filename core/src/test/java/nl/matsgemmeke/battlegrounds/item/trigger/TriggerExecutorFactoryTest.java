package nl.matsgemmeke.battlegrounds.item.trigger;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impl.EnemyHitTrigger;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriggerExecutorFactoryTest {

    private static final double RANGE = 2.5;
    private static final long DELAY = 10L;
    private static final long INTERVAL = 2L;

    @Mock
    private Provider<EnemyHitTrigger> enemyHitTriggerProvider;
    @Mock
    private Provider<EnemyProximityTrigger> enemyProximityTriggerProvider;
    @Mock
    private Scheduler scheduler;

    private TriggerExecutorFactory factory;

    @BeforeEach
    void setUp() {
        factory = new TriggerExecutorFactory(enemyHitTriggerProvider, enemyProximityTriggerProvider, scheduler);
    }

    static Stream<Arguments> invalidTriggerSpecCases() {
        return Stream.of(
                arguments("BLOCK_IMPACT", null, INTERVAL, null, null, "delay"),
                arguments("BLOCK_IMPACT", DELAY, null, null, null, "interval"),
                arguments("ENEMY_HIT", null, INTERVAL, null, RANGE, "delay"),
                arguments("ENEMY_HIT", DELAY, null, null, RANGE, "interval"),
                arguments("ENEMY_PROXIMITY", null, INTERVAL, null, RANGE, "delay"),
                arguments("ENEMY_PROXIMITY", DELAY, null, null, RANGE, "interval"),
                arguments("ENEMY_PROXIMITY", DELAY, INTERVAL, null, null, "range"),
                arguments("FLOOR_HIT", null, INTERVAL, null, null, "delay"),
                arguments("FLOOR_HIT", DELAY, null, null, null, "interval"),
                arguments("SCHEDULED", null, null, null, null, "offsetDelays")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTriggerSpecCases")
    void createThrowsTriggerCreationExceptionWhenRequiredValuesInSpecAreNull(String type, Long delay, Long interval, List<Long> offsetDelays, Double range, String requiredValue) {
        TriggerSpec spec = new TriggerSpec();
        spec.type = type;
        spec.delay = delay;
        spec.interval = interval;
        spec.offsetDelays = offsetDelays;
        spec.range = range;

        String expectedErrorMessage = "Cannot create trigger %s because of invalid spec: Required '%s' value is missing".formatted(type, requiredValue);

        assertThatThrownBy(() -> factory.create(spec))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage(expectedErrorMessage);
    }

    @Test
    void createReturnsTriggerExecutorWithBlockImpactTriggerInstanceWhenTriggerTypeEqualsBlockImpact() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "BLOCK_IMPACT";
        spec.delay = DELAY;
        spec.interval = INTERVAL;

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isFalse();
    }

    @Test
    void createReturnsTriggerExecutorWithEnemyHitTriggerWhenTriggerTypeEqualsEnemyHit() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "ENEMY_HIT";
        spec.delay = DELAY;
        spec.interval = INTERVAL;
        spec.repeating = true;

        EnemyHitTrigger trigger = mock(EnemyHitTrigger.class);
        when(enemyHitTriggerProvider.get()).thenReturn(trigger);

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isTrue();
    }

    @Test
    void createReturnsTriggerExecutorWithEnemyProximityTriggerInstanceWhenTripperTypeEqualsEnemyProximity() {
        TriggerSpec spec = new TriggerSpec();
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
    void createReturnsTriggerExecutorWithFloorHitTriggerInstanceWhenTriggerTypeEqualsFloorHit() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "FLOOR_HIT";
        spec.delay = DELAY;
        spec.interval = INTERVAL;
        spec.range = RANGE;

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isFalse();
    }

    @Test
    void createReturnsTriggerExecutorWithScheduledTriggerInstanceWithSingleRunScheduleWhenTriggerTypeEqualsDelayed() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "SCHEDULED";
        spec.offsetDelays = List.of(DELAY);

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isFalse();
    }

    @Test
    void createReturnsTriggerExecutorWithScheduledTriggerInstanceWithSequenceScheduleWhenTriggerTypeEqualsDelayed() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "SCHEDULED";
        spec.offsetDelays = List.of(10L, 20L);

        TriggerExecutor triggerExecutor = factory.create(spec);

        assertThat(triggerExecutor.isRepeating()).isFalse();
    }
}
