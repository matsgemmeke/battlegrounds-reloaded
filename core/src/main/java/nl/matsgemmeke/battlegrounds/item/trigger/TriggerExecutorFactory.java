package nl.matsgemmeke.battlegrounds.item.trigger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.*;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.floor.FloorHitTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impl.BlockImpactTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impl.EntityImpactTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.scheduled.ScheduledTrigger;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;

import java.util.List;
import java.util.function.Supplier;

public class TriggerExecutorFactory {

    private final Provider<EnemyProximityTrigger> enemyProximityTriggerProvider;
    private final Provider<EntityImpactTrigger> entityImpactTriggerProvider;
    private final Scheduler scheduler;

    @Inject
    public TriggerExecutorFactory(
            Provider<EnemyProximityTrigger> enemyProximityTriggerProvider,
            Provider<EntityImpactTrigger> entityImpactTriggerProvider,
            Scheduler scheduler
    ) {
        this.enemyProximityTriggerProvider = enemyProximityTriggerProvider;
        this.entityImpactTriggerProvider = entityImpactTriggerProvider;
        this.scheduler = scheduler;
    }

    public TriggerExecutor create(TriggerSpec triggerSpec) {
        TriggerType triggerType = TriggerType.valueOf(triggerSpec.type);

        TriggerExecutor triggerExecutor = switch (triggerType) {
            case BLOCK_IMPACT -> {
                BlockImpactTriggerSpec spec = (BlockImpactTriggerSpec) triggerSpec;

                BlockImpactTrigger trigger = new BlockImpactTrigger();
                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(spec.delay, spec.interval);

                yield new TriggerExecutor(trigger, scheduleSupplier);
            }
            case ENEMY_PROXIMITY -> {
                EnemyProximityTriggerSpec spec = (EnemyProximityTriggerSpec) triggerSpec;

                EnemyProximityTrigger trigger = enemyProximityTriggerProvider.get();
                trigger.setCheckingRange(spec.range);

                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(spec.delay, spec.interval);

                yield new TriggerExecutor(trigger, scheduleSupplier);
            }
            case ENTITY_IMPACT -> {
                EntityImpactTriggerSpec spec = (EntityImpactTriggerSpec) triggerSpec;

                EntityImpactTrigger trigger = entityImpactTriggerProvider.get();
                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(spec.delay, spec.interval);

                yield new TriggerExecutor(trigger, scheduleSupplier);
            }
            case FLOOR_HIT -> {
                FloorHitTriggerSpec spec = (FloorHitTriggerSpec) triggerSpec;

                FloorHitTrigger trigger = new FloorHitTrigger();
                Supplier<Schedule> scheduleSupplier = () -> scheduler.createRepeatingSchedule(spec.delay, spec.interval);

                yield new TriggerExecutor(trigger, scheduleSupplier);
            }
            case SCHEDULED -> {
                ScheduledTriggerSpec spec = (ScheduledTriggerSpec) triggerSpec;
                List<Long> offsetDelays = spec.offsetDelays;

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

        boolean repeating = Boolean.TRUE.equals(triggerSpec.repeating);

        triggerExecutor.setRepeating(repeating);
        return triggerExecutor;
    }
}
