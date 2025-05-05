package nl.matsgemmeke.battlegrounds.item.effect.trigger.enemy;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.BaseTrigger;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnemyProximityTrigger extends BaseTrigger {

    private static final long RUNNABLE_DELAY = 0L;

    private boolean primed;
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
        this.primed = false;
    }

    public boolean isPrimed() {
        return primed;
    }

    public void cancel() {
        if (task == null) {
            return;
        }

        primed = false;
        task.cancel();
    }

    public void prime(@NotNull ItemEffectContext context) {
        primed = true;
        task = taskRunner.runTaskTimer(() -> this.runCheck(context), RUNNABLE_DELAY, periodBetweenChecks);
    }

    private void runCheck(@NotNull ItemEffectContext context) {
        ItemEffectSource source = context.getSource();

        if (!source.exists()) {
            task.cancel();
            return;
        }

        List<GameEntity> targets = targetFinder.findEnemyTargets(context.getEntity().getUniqueId(), source.getLocation(), checkingRange);

        if (targets.isEmpty()) {
            return;
        }

        this.notifyObservers();
    }
}
