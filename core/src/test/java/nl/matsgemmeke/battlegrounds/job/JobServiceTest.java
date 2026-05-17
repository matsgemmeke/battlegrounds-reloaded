package nl.matsgemmeke.battlegrounds.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Spy
    private Clock clock = Clock.fixed(Instant.parse("2026-05-17T13:30:00.00Z"), ZoneOffset.UTC);
    @Mock
    private ScheduledExecutorService scheduledExecutorService;
    @InjectMocks
    private JobService jobService;

    @Test
    @DisplayName("schedule schedules given job to run every 1 hour")
    void schedule() {
        Job job = mock(Job.class);

        jobService.schedule(job);

        verify(scheduledExecutorService).scheduleAtFixedRate(job, 1800000L, 3600000L, TimeUnit.MILLISECONDS);
    }
}
