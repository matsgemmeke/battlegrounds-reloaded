package nl.matsgemmeke.battlegrounds.scheduling;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.List;

public class Scheduler {

    private final Provider<RepeatingSchedule> repeatingScheduleProvider;
    private final Provider<SequenceSchedule> sequenceScheduleProvider;
    private final Provider<SingleRunSchedule> singleRunScheduleProvider;

    @Inject
    public Scheduler(Provider<RepeatingSchedule> repeatingScheduleProvider, Provider<SequenceSchedule> sequenceScheduleProvider, Provider<SingleRunSchedule> singleRunScheduleProvider) {
        this.repeatingScheduleProvider = repeatingScheduleProvider;
        this.sequenceScheduleProvider = sequenceScheduleProvider;
        this.singleRunScheduleProvider = singleRunScheduleProvider;
    }

    public Schedule createRepeatingSchedule(long delay, long interval) {
        RepeatingSchedule schedule = repeatingScheduleProvider.get();
        schedule.setDelay(delay);
        schedule.setInterval(interval);
        return schedule;
    }

    public Schedule createRepeatingSchedule(long delay, long interval, long duration) {
        RepeatingSchedule schedule = repeatingScheduleProvider.get();
        schedule.setDelay(delay);
        schedule.setInterval(interval);
        schedule.setDuration(duration);
        return schedule;
    }

    public Schedule createSequenceSchedule(List<Long> offsetTicks) {
        SequenceSchedule schedule = sequenceScheduleProvider.get();
        schedule.setOffsetTicks(offsetTicks);
        return schedule;
    }

    public Schedule createSingleRunSchedule(long delay) {
        SingleRunSchedule schedule = singleRunScheduleProvider.get();
        schedule.setDelay(delay);
        return schedule;
    }
}
