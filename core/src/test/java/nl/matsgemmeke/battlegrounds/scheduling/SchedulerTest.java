package nl.matsgemmeke.battlegrounds.scheduling;

import com.google.inject.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {

    private static final long DELAY = 5L;
    private static final long INTERVAL = 1L;
    private static final long DURATION = 10L;
    private static final List<Long> OFFSET_TICKS = List.of(5L, 10L);

    @Mock
    private Provider<RepeatingSchedule> repeatingScheduleProvider;
    @Mock
    private Provider<SequenceSchedule> sequenceScheduleProvider;
    @Mock
    private Provider<SingleRunSchedule> singleRunScheduleProvider;

    private Scheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new Scheduler(repeatingScheduleProvider, sequenceScheduleProvider, singleRunScheduleProvider);
    }

    @Test
    void createRepeatingScheduleReturnsRepeatingScheduleInstance() {
        RepeatingSchedule repeatingSchedule = mock(RepeatingSchedule.class);
        when(repeatingScheduleProvider.get()).thenReturn(repeatingSchedule);

        Schedule schedule = scheduler.createRepeatingSchedule(DELAY, INTERVAL);

        assertThat(schedule).isEqualTo(repeatingSchedule);

        verify(repeatingSchedule).setDelay(DELAY);
        verify(repeatingSchedule).setInterval(INTERVAL);
        verify(repeatingSchedule, never()).setDuration(anyLong());
    }

    @Test
    void createRepeatingScheduleReturnsRepeatingScheduleInstanceWithDuration() {
        RepeatingSchedule repeatingSchedule = mock(RepeatingSchedule.class);
        when(repeatingScheduleProvider.get()).thenReturn(repeatingSchedule);

        Schedule schedule = scheduler.createRepeatingSchedule(DELAY, INTERVAL, DURATION);

        assertThat(schedule).isEqualTo(repeatingSchedule);

        verify(repeatingSchedule).setDelay(DELAY);
        verify(repeatingSchedule).setInterval(INTERVAL);
        verify(repeatingSchedule).setDuration(DURATION);
    }

    @Test
    void createSequenceScheduleReturnsSequenceScheduleInstance() {
        SequenceSchedule sequenceSchedule = mock(SequenceSchedule.class);
        when(sequenceScheduleProvider.get()).thenReturn(sequenceSchedule);

        Schedule schedule = scheduler.createSequenceSchedule(OFFSET_TICKS);

        assertThat(schedule).isEqualTo(sequenceSchedule);

        verify(sequenceSchedule).setOffsetTicks(OFFSET_TICKS);
    }

    @Test
    void createSingleRunScheduleReturnsSingleRunScheduleInstance() {
        SingleRunSchedule singleRunSchedule = mock(SingleRunSchedule.class);
        when(singleRunScheduleProvider.get()).thenReturn(singleRunSchedule);

        Schedule schedule = scheduler.createSingleRunSchedule(DELAY);

        assertThat(schedule).isEqualTo(singleRunSchedule);

        verify(singleRunSchedule).setDelay(DELAY);
    }
}
