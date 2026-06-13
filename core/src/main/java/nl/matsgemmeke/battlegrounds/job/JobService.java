package nl.matsgemmeke.battlegrounds.job;

import com.google.inject.Inject;

import java.time.Clock;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobService {

    private final Clock clock;
    private final ScheduledExecutorService scheduledExecutorService;

    @Inject
    public JobService(Clock clock, ScheduledExecutorService scheduledExecutorService) {
        this.clock = clock;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void schedule(Job job, long periodMillis) {
        long nowMillis = clock.millis();
        long millisIntoPeriod = nowMillis % periodMillis;
        long initialDelay = periodMillis - millisIntoPeriod;

        scheduledExecutorService.scheduleAtFixedRate(job, initialDelay, periodMillis, TimeUnit.MILLISECONDS);
    }
}
