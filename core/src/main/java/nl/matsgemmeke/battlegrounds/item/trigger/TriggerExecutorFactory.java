package nl.matsgemmeke.battlegrounds.item.trigger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.floor.FloorHitTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impl.BlockImpactTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impl.EnemyHitTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impl.EntityImpactTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.scheduled.ScheduledTrigger;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class TriggerExecutorFactory {

    private final Provider<EnemyHitTrigger> enemyHitTriggerProvider;
    private final Provider<EnemyProximityTrigger> enemyProximityTriggerProvider;
    private final Scheduler scheduler;

    @Inject
    public TriggerExecutorFactory(Provider<EnemyHitTrigger> enemyHitTriggerProvider, Provider<EnemyProximityTrigger> enemyProximityTriggerProvider, Scheduler scheduler) {
        this.enemyHitTriggerProvider = enemyHitTriggerProvider;
        this.enemyProximityTriggerProvider = enemyProximityTriggerProvider;
        this.scheduler = scheduler;
    }

    public TriggerExecutor create(TriggerSpec spec) {
        TriggerType triggerType = TriggerType.valueOf(spec.type);

        TriggerExecutor triggerExecutor = switch (triggerType) {
            case BLOCK_IMPACT -> {
                Long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                Long interval = this.validateSpecVar(spec.interval, "interval", triggerType);

                BlockImpactTrigger trigger = new BlockImpactTrigger();
                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(delay, interval);

                yield new TriggerExecutor(trigger, scheduleSupplier);
            }
            case ENEMY_HIT -> {
                long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                long interval = this.validateSpecVar(spec.interval, "interval", triggerType);

                EnemyHitTrigger trigger = enemyHitTriggerProvider.get();
                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(delay, interval);

                yield new TriggerExecutor(trigger, scheduleSupplier);
            }
            case ENEMY_PROXIMITY -> {
                long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                long interval = this.validateSpecVar(spec.interval, "interval", triggerType);
                double range = this.validateSpecVar(spec.range, "range", triggerType);

                EnemyProximityTrigger trigger = enemyProximityTriggerProvider.get();
                trigger.setCheckingRange(range);

                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(delay, interval);

                yield new TriggerExecutor(trigger, scheduleSupplier);
            }
            case ENTITY_IMPACT -> {
                long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                long interval = this.validateSpecVar(spec.interval, "interval", triggerType);

                EntityImpactTrigger trigger = new EntityImpactTrigger();
                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(delay, interval);

                yield new TriggerExecutor(trigger, scheduleSupplier);
            }
            case FLOOR_HIT -> {
                long delay = this.validateSpecVar(spec.delay, "delay", triggerType);
                long interval = this.validateSpecVar(spec.interval, "interval", triggerType);

                FloorHitTrigger trigger = new FloorHitTrigger();
                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(delay, interval);

                yield new TriggerExecutor(trigger, scheduleSupplier);
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

                yield new TriggerExecutor(trigger, scheduleSupplier);
            }
        };

        boolean repeating = Boolean.TRUE.equals(spec.repeating);

        triggerExecutor.setRepeating(repeating);
        return triggerExecutor;
    }

    private <T> T validateSpecVar(@Nullable T value, String valueName, Object triggerType) {
        if (value == null) {
            throw new TriggerCreationException("Cannot create trigger %s because of invalid spec: Required '%s' value is missing".formatted(triggerType, valueName));
        }

        return value;
    }
}
