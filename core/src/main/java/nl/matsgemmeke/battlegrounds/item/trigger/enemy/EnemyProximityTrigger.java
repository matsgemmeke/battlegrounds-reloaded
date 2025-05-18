package nl.matsgemmeke.battlegrounds.item.trigger.enemy;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.BaseTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnemyProximityTrigger extends BaseTrigger {

    private static final long RUNNABLE_DELAY = 0L;

    private boolean activated;
    private BukkitTask task;
    private final double checkingRange;
    private final long periodBetweenChecks;
    @NotNull
    private final TargetFinder targetFinder;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public EnemyProximityTrigger(
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull TargetFinder targetFinder,
            @Assisted double checkingRange,
            @Assisted long periodBetweenChecks
    ) {
        this.targetFinder = targetFinder;
        this.taskRunner = taskRunner;
        this.checkingRange = checkingRange;
        this.periodBetweenChecks = periodBetweenChecks;
        this.activated = false;
    }

    public boolean isActivated() {
        return activated;
    }

    public void activate(@NotNull TriggerContext context) {
        activated = true;
        task = taskRunner.runTaskTimer(() -> this.runCheck(context), RUNNABLE_DELAY, periodBetweenChecks);
    }

    private void runCheck(@NotNull TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            task.cancel();
            return;
        }

        List<GameEntity> targets = targetFinder.findEnemyTargets(context.deployerEntity().getUniqueId(), target.getLocation(), checkingRange);

        if (targets.isEmpty()) {
            return;
        }

        this.notifyObservers();
    }

    public void deactivate() {
        if (task == null) {
            return;
        }

        activated = false;
        task.cancel();
    }
}
