package nl.matsgemmeke.battlegrounds.item.trigger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.floor.FloorHitTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impact.ImpactTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.scheduled.ScheduledTrigger;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class TriggerExecutorFactory {

    @NotNull
    private final Provider<EnemyProximityTrigger> enemyProximityTriggerProvider;
    @NotNull
    private final Scheduler scheduler;

    @Inject
    public TriggerExecutorFactory(@NotNull Provider<EnemyProximityTrigger> enemyProximityTriggerProvider, @NotNull Scheduler scheduler) {
        this.enemyProximityTriggerProvider = enemyProximityTriggerProvider;
        this.scheduler = scheduler;
    }

    public TriggerExecutor create(TriggerSpec spec) {
        TriggerType triggerType = TriggerType.valueOf(spec.type);

        switch (triggerType) {
            case ENEMY_PROXIMITY -> {
                long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                long interval = this.validateSpecVar(spec.interval, "interval", triggerType);
                double range = this.validateSpecVar(spec.range, "range", triggerType);

                EnemyProximityTrigger trigger = enemyProximityTriggerProvider.get();
                trigger.setCheckingRange(range);

                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(delay, interval);

                return new TriggerExecutor(trigger, scheduleSupplier);
            }
            case FLOOR_HIT -> {
                long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                long interval = this.validateSpecVar(spec.interval, "interval", triggerType);

                FloorHitTrigger trigger = new FloorHitTrigger();
                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(delay, interval);

                return new TriggerExecutor(trigger, scheduleSupplier);
            }
            case IMPACT -> {
                Long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                Long interval = this.validateSpecVar(spec.interval, "interval", triggerType);

                ImpactTrigger trigger = new ImpactTrigger();
                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(delay, interval);

                return new TriggerExecutor(trigger, scheduleSupplier);
            }
            case SCHEDULED -> {
                List<Long> offsetDelays = this.validateSpecVar(spec.offsetDelays, "offsetDelays", triggerType);

                ScheduledTrigger trigger = new ScheduledTrigger();
                Supplier<Schedule> scheduleSupplier;
                boolean continuous = offsetDelays.size() > 1;

                if (continuous) {
                    scheduleSupplier = () -> scheduler.createSequenceSchedule(offsetDelays);
                } else {
                    scheduleSupplier = () -> scheduler.createSingleRunSchedule(offsetDelays.get(0));
                }

                return new TriggerExecutor(trigger, scheduleSupplier);
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
