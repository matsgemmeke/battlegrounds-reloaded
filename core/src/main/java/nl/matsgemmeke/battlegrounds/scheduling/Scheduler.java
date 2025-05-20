package nl.matsgemmeke.battlegrounds.scheduling;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Scheduler {

    @NotNull
    private final Provider<RepeatingSchedule> repeatingScheduleProvider;
    @NotNull
    private final Provider<SequenceSchedule> sequenceScheduleProvider;

    @Inject
    public Scheduler(@NotNull Provider<RepeatingSchedule> repeatingScheduleProvider, @NotNull Provider<SequenceSchedule> sequenceScheduleProvider) {
        this.repeatingScheduleProvider = repeatingScheduleProvider;
        this.sequenceScheduleProvider = sequenceScheduleProvider;
    }

    @NotNull
    public Schedule createSequenceSchedule(@NotNull List<Long> offsetTicks) {
        SequenceSchedule schedule = sequenceScheduleProvider.get();
        schedule.setOffsetTicks(offsetTicks);
        return schedule;
    }

    @NotNull
    public Schedule createRepeatingSchedule(long delay, long interval) {
        RepeatingSchedule schedule = repeatingScheduleProvider.get();
        schedule.setDelay(delay);
        schedule.setInterval(interval);
        return schedule;
    }
}
