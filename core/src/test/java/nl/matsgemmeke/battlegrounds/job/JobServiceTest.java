package nl.matsgemmeke.battlegrounds.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    private Clock clock = Clock.fixed(Instant.parse("2026-05-17T13:00:30.00Z"), ZoneOffset.UTC);
    @Mock
    private ScheduledExecutorService scheduledExecutorService;
    @InjectMocks
    private JobService jobService;

    @ParameterizedTest
    @CsvSource({
            "5000,5000", // 5-second period, 5-second initial delay
            "60000,30000", // 1-minute period, 30-second initial delay
            "300000,270000", // 5-minute period, 4-minute and 30-second initial delay
            "3600000,3570000" // 1-hour period, 59-minute and 30-second initial delay
    })
    @DisplayName("schedule schedules given job to run every given period, with a matching initial delay")
    void schedule(long periodMillis, long expectedInitialDelay) {
        Job job = mock(Job.class);

        jobService.schedule(job, periodMillis);

        verify(scheduledExecutorService).scheduleAtFixedRate(job, expectedInitialDelay, periodMillis, TimeUnit.MILLISECONDS);
    }
}
