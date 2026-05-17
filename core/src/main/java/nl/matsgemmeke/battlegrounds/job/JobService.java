package nl.matsgemmeke.battlegrounds.job;

import com.google.inject.Inject;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobService {

    private final static long HOUR_MILLIS_PERIOD = 60 * 60 * 1000;

    private final Clock clock;
    private final ScheduledExecutorService scheduledExecutorService;

    @Inject
    public JobService(Clock clock, ScheduledExecutorService scheduledExecutorService) {
        this.clock = clock;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void schedule(Job job) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime nextHour = now.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        long millisToNextHour = now.until(nextHour, ChronoUnit.MILLIS);

        scheduledExecutorService.scheduleAtFixedRate(job, millisToNextHour, HOUR_MILLIS_PERIOD, TimeUnit.MILLISECONDS);
    }
}
