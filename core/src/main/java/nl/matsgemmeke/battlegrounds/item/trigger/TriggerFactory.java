package nl.matsgemmeke.battlegrounds.item.trigger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.floor.FloorHitTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impact.ImpactTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.scheduled.ScheduledTrigger;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TriggerFactory {

    @NotNull
    private final Provider<TargetFinder> targetFinderProvider;
    @NotNull
    private final Scheduler scheduler;

    @Inject
    public TriggerFactory(@NotNull Provider<TargetFinder> targetFinderProvider, @NotNull Scheduler scheduler) {
        this.targetFinderProvider = targetFinderProvider;
        this.scheduler = scheduler;
    }

    public Trigger create(TriggerSpec spec) {
        TriggerType triggerType = TriggerType.valueOf(spec.type);

        switch (triggerType) {
            case ENEMY_PROXIMITY -> {
                long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                long interval = this.validateSpecVar(spec.interval, "interval", triggerType);
                double range = this.validateSpecVar(spec.range, "range", triggerType);

                Schedule schedule = scheduler.createRepeatingSchedule(delay, interval);
                TargetFinder targetFinder = targetFinderProvider.get();

                return new EnemyProximityTrigger(schedule, targetFinder, range);
            }
            case FLOOR_HIT -> {
                long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                long interval = this.validateSpecVar(spec.interval, "interval", triggerType);
                Schedule schedule = scheduler.createRepeatingSchedule(delay, interval);

                return new FloorHitTrigger(schedule);
            }
            case IMPACT -> {
                Long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                Long interval = this.validateSpecVar(spec.interval, "interval", triggerType);
                Schedule schedule = scheduler.createRepeatingSchedule(delay, interval);

                return new ImpactTrigger(schedule);
            }
            case SCHEDULED -> {
                List<Long> offsetDelays = this.validateSpecVar(spec.offsetDelays, "offsetDelays", triggerType);
                Schedule schedule;

                boolean continuous = offsetDelays.size() > 1;

                if (continuous) {
                    schedule = scheduler.createSequenceSchedule(offsetDelays);
                } else {
                    schedule = scheduler.createSingleRunSchedule(offsetDelays.get(0));
                }

                return new ScheduledTrigger(schedule, continuous);
            }
        }

        throw new TriggerCreationException("Unknown trigger type '%s'".formatted(spec.type));
    }

    private <T> T validateSpecVar(@Nullable T value, @NotNull String valueName, @NotNull Object triggerType) {
        if (value == null) {
            throw new TriggerCreationException("Cannot create trigger %s because of invalid spec: Required '%s' value is missing".formatted(triggerType, valueName));
        }

        return value;
    }
}
