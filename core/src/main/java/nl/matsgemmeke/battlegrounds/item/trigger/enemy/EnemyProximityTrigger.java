package nl.matsgemmeke.battlegrounds.item.trigger.enemy;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.BaseTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnemyProximityTrigger extends BaseTrigger {

    private final double checkingRange;
    @NotNull
    private final Schedule schedule;
    @NotNull
    private final TargetFinder targetFinder;
    private boolean started;

    public EnemyProximityTrigger(@NotNull Schedule schedule, @NotNull TargetFinder targetFinder, double checkingRange) {
        this.schedule = schedule;
        this.targetFinder = targetFinder;
        this.checkingRange = checkingRange;
        this.started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public void start(@NotNull TriggerContext context) {
        schedule.addTask(() -> this.runCheck(context));
        schedule.start();
        started = true;
    }

    private void runCheck(@NotNull TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            this.stop();
            return;
        }

        List<GameEntity> targets = targetFinder.findEnemyTargets(context.entity().getUniqueId(), target.getLocation(), checkingRange);

        if (targets.isEmpty()) {
            return;
        }

        this.notifyObservers();
    }

    public void stop() {
        schedule.stop();
        started = false;
    }
}
