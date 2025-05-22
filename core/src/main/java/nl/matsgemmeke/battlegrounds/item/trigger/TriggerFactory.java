package nl.matsgemmeke.battlegrounds.item.trigger;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.floor.FloorHitTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.impact.ImpactTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.timed.TimedTrigger;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TriggerFactory {

    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final Scheduler scheduler;

    @Inject
    public TriggerFactory(@NotNull GameContextProvider contextProvider, @NotNull Scheduler scheduler) {
        this.contextProvider = contextProvider;
        this.scheduler = scheduler;
    }

    @NotNull
    public Trigger create(@NotNull TriggerSpec spec, @NotNull GameKey gameKey) {
        TriggerType triggerType = TriggerType.valueOf(spec.type());

        switch (triggerType) {
            case ENEMY_PROXIMITY -> {
                long delay = this.validateNotNull(spec.delay(), "delay", triggerType);
                long interval = this.validateNotNull(spec.interval(), "interval", triggerType);
                double range = this.validateNotNull(spec.range(), "range", triggerType);

                Schedule schedule = scheduler.createRepeatingSchedule(delay, interval);
                TargetFinder targetFinder = contextProvider.getComponent(gameKey, TargetFinder.class);

                return new EnemyProximityTrigger(schedule, targetFinder, range);
            }
            case FLOOR_HIT -> {
                long delay = this.validateNotNull(spec.delay(), "delay", triggerType);
                long interval = this.validateNotNull(spec.interval(), "interval", triggerType);
                Schedule schedule = scheduler.createRepeatingSchedule(delay, interval);

                return new FloorHitTrigger(schedule);
            }
            case IMPACT -> {
                Long delay = this.validateNotNull(spec.delay(), "delay", triggerType);
                Long interval = this.validateNotNull(spec.interval(), "interval", triggerType);
                Schedule schedule = scheduler.createRepeatingSchedule(delay, interval);

                return new ImpactTrigger(schedule);
            }
            case TIMED -> {
                long delay = this.validateNotNull(spec.delay(), "delay", triggerType);
                Schedule schedule = scheduler.createSingleRunSchedule(delay);

                return new TimedTrigger(schedule);
            }
        }

        throw new TriggerCreationException("Unknown trigger type '%s'".formatted(spec.type()));
    }

    private <T> T validateNotNull(@Nullable T value, @NotNull String valueName, @NotNull Object triggerType) {
        if (value == null) {
            throw new TriggerCreationException("Cannot create trigger %s because of invalid spec: Required '%s' value is missing".formatted(triggerType, valueName));
        }

        return value;
    }
}
