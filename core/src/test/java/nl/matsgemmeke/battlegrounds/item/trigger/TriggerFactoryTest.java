package nl.matsgemmeke.battlegrounds.item.trigger;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.floor.FloorHitTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impact.ImpactTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.scheduled.ScheduledTrigger;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerFactoryTest {

    private static final double RANGE = 2.5;
    private static final long DELAY = 10L;
    private static final long INTERVAL = 2L;

    private Provider<TargetFinder> targetFinderProvider;
    private Scheduler scheduler;

    @BeforeEach
    public void setUp() {
        targetFinderProvider = mock();
        scheduler = mock(Scheduler.class);
    }

    private static Stream<Arguments> invalidTriggerSpecCases() {
        return Stream.of(
                arguments("ENEMY_PROXIMITY", null, INTERVAL, null, RANGE, "delay"),
                arguments("ENEMY_PROXIMITY", DELAY, null, null, RANGE, "interval"),
                arguments("ENEMY_PROXIMITY", DELAY, INTERVAL, null, null, "range"),
                arguments("FLOOR_HIT", null, INTERVAL, null, null, "delay"),
                arguments("FLOOR_HIT", DELAY, null, null, null, "interval"),
                arguments("IMPACT", null, INTERVAL, null, null, "delay"),
                arguments("IMPACT", DELAY, null, null, null, "interval"),
                arguments("SCHEDULED", null, null, null, null, "offsetDelays")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTriggerSpecCases")
    public void createThrowsTriggerCreationExceptionWhenRequiredValuesInSpecAreNull(String type, Long delay, Long interval, List<Long> offsetDelays, Double range, String requiredValue) {
        TriggerSpec spec = new TriggerSpec();
        spec.type = type;
        spec.delay = delay;
        spec.interval = interval;
        spec.offsetDelays = offsetDelays;
        spec.range = range;

        String expectedErrorMessage = "Cannot create trigger %s because of invalid spec: Required '%s' value is missing".formatted(type, requiredValue);

        TriggerFactory factory = new TriggerFactory(targetFinderProvider, scheduler);

        assertThatThrownBy(() -> factory.create(spec))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage(expectedErrorMessage);
    }

    @Test
    public void createReturnsEnemyProximityTriggerInstanceWhenTripperTypeEqualsEnemyProximity() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "ENEMY_PROXIMITY";
        spec.delay = DELAY;
        spec.interval = INTERVAL;
        spec.range = RANGE;

        Schedule schedule = mock(Schedule.class);
        when(scheduler.createRepeatingSchedule(DELAY, INTERVAL)).thenReturn(schedule);

        TargetFinder targetFinder = mock(TargetFinder.class);
        when(targetFinderProvider.get()).thenReturn(targetFinder);

        TriggerFactory factory = new TriggerFactory(targetFinderProvider, scheduler);
        Trigger trigger = factory.create(spec);

        assertThat(trigger).isInstanceOf(EnemyProximityTrigger.class);
    }

    @Test
    public void createReturnsFloorHitTriggerInstanceWhenTriggerTypeEqualsFloorHit() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "FLOOR_HIT";
        spec.delay = DELAY;
        spec.interval = INTERVAL;
        spec.range = RANGE;

        Schedule schedule = mock(Schedule.class);
        when(scheduler.createRepeatingSchedule(DELAY, INTERVAL)).thenReturn(schedule);

        TriggerFactory factory = new TriggerFactory(targetFinderProvider, scheduler);
        Trigger trigger = factory.create(spec);

        assertThat(trigger).isInstanceOf(FloorHitTrigger.class);
    }

    @Test
    public void createReturnsImpactTriggerInstanceWhenTriggerTypeEqualsImpact() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "IMPACT";
        spec.delay = DELAY;
        spec.interval = INTERVAL;

        Schedule schedule = mock(Schedule.class);
        when(scheduler.createRepeatingSchedule(DELAY, INTERVAL)).thenReturn(schedule);

        TriggerFactory factory = new TriggerFactory(targetFinderProvider, scheduler);
        Trigger trigger = factory.create(spec);

        assertThat(trigger).isInstanceOf(ImpactTrigger.class);
    }

    @Test
    public void createReturnsScheduledTriggerInstanceWithSingleRunScheduleWhenTriggerTypeEqualsDelayed() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "SCHEDULED";
        spec.offsetDelays = List.of(DELAY);

        Schedule schedule = mock(Schedule.class);
        when(scheduler.createSingleRunSchedule(DELAY)).thenReturn(schedule);

        TriggerFactory factory = new TriggerFactory(targetFinderProvider, scheduler);
        Trigger trigger = factory.create(spec);

        assertThat(trigger).isInstanceOf(ScheduledTrigger.class);
    }

    @Test
    public void createReturnsScheduledTriggerInstanceWithSequenceScheduleWhenTriggerTypeEqualsDelayed() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "SCHEDULED";
        spec.offsetDelays = List.of(10L, 20L);

        Schedule schedule = mock(Schedule.class);
        when(scheduler.createSequenceSchedule(spec.offsetDelays)).thenReturn(schedule);

        TriggerFactory factory = new TriggerFactory(targetFinderProvider, scheduler);
        Trigger trigger = factory.create(spec);

        assertThat(trigger).isInstanceOf(ScheduledTrigger.class);
    }
}
