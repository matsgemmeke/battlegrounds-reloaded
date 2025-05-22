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
    @NotNull
    private final Provider<SingleRunSchedule> singleRunScheduleProvider;

    @Inject
    public Scheduler(
            @NotNull Provider<RepeatingSchedule> repeatingScheduleProvider,
            @NotNull Provider<SequenceSchedule> sequenceScheduleProvider,
            @NotNull Provider<SingleRunSchedule> singleRunScheduleProvider
    ) {
        this.repeatingScheduleProvider = repeatingScheduleProvider;
        this.sequenceScheduleProvider = sequenceScheduleProvider;
        this.singleRunScheduleProvider = singleRunScheduleProvider;
    }

    @NotNull
    public Schedule createRepeatingSchedule(long delay, long interval) {
        RepeatingSchedule schedule = repeatingScheduleProvider.get();
        schedule.setDelay(delay);
        schedule.setInterval(interval);
        return schedule;
    }

    @NotNull
    public Schedule createSequenceSchedule(@NotNull List<Long> offsetTicks) {
        SequenceSchedule schedule = sequenceScheduleProvider.get();
        schedule.setOffsetTicks(offsetTicks);
        return schedule;
    }

    @NotNull
    public Schedule createSingleRunSchedule(long delay) {
        SingleRunSchedule schedule = singleRunScheduleProvider.get();
        schedule.setDelay(delay);
        return schedule;
    }
}
