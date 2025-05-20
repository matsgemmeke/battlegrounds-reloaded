package nl.matsgemmeke.battlegrounds.scheduling;

import com.google.inject.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SchedulerTest {

    private Provider<RepeatingSchedule> repeatingScheduleProvider;
    private Provider<SequenceSchedule> sequenceScheduleProvider;

    @BeforeEach
    public void setUp() {
        repeatingScheduleProvider = mock();
        sequenceScheduleProvider = mock();
    }

    @Test
    public void createRepeatingScheduleReturnsRepeatingScheduleInstance() {
        long delay = 5L;
        long interval = 1L;

        RepeatingSchedule repeatingSchedule = mock(RepeatingSchedule.class);
        when(repeatingScheduleProvider.get()).thenReturn(repeatingSchedule);

        Scheduler scheduler = new Scheduler(repeatingScheduleProvider, sequenceScheduleProvider);
        Schedule schedule = scheduler.createRepeatingSchedule(delay, interval);

        assertThat(schedule).isEqualTo(repeatingSchedule);

        verify(repeatingSchedule).setDelay(delay);
        verify(repeatingSchedule).setInterval(interval);
    }

    @Test
    public void createSequenceScheduleReturnsSequenceScheduleInstance() {
        List<Long> offsetTicks = List.of(5L, 10L);

        SequenceSchedule sequenceSchedule = mock(SequenceSchedule.class);
        when(sequenceScheduleProvider.get()).thenReturn(sequenceSchedule);

        Scheduler scheduler = new Scheduler(repeatingScheduleProvider, sequenceScheduleProvider);
        Schedule schedule = scheduler.createSequenceSchedule(offsetTicks);

        assertThat(schedule).isEqualTo(sequenceSchedule);

        verify(sequenceSchedule).setOffsetTicks(offsetTicks);
    }
}
