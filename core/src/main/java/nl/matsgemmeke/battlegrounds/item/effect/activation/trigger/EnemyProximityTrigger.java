package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnemyProximityTrigger implements Trigger {

    private static final long RUNNABLE_DELAY = 0L;

    private BukkitTask task;
    private double checkingRange;
    @NotNull
    private List<TriggerObserver> observers;
    private long periodBetweenChecks;
    @NotNull
    private TargetFinder targetFinder;
    @NotNull
    private TaskRunner taskRunner;

    public EnemyProximityTrigger(
            @NotNull TargetFinder targetFinder,
            @NotNull TaskRunner taskRunner,
            double checkingRange,
            long periodBetweenChecks
    ) {
        this.targetFinder = targetFinder;
        this.taskRunner = taskRunner;
        this.checkingRange = checkingRange;
        this.periodBetweenChecks = periodBetweenChecks;
        this.observers = new ArrayList<>();
    }

    public void addObserver(@NotNull TriggerObserver observer) {
        observers.add(observer);
    }

    public void cancel() {
        if (task == null) {
            return;
        }

        task.cancel();
    }

    public void checkTriggerActivation(@NotNull ItemEffectContext context) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(context), RUNNABLE_DELAY, periodBetweenChecks);
    }

    private void runCheck(@NotNull ItemEffectContext context) {
        ItemEffectSource source = context.getSource();

        if (!source.exists()) {
            task.cancel();
            return;
        }

        List<GameEntity> targets = targetFinder.findEnemyTargets(context.getHolder(), source.getLocation(), checkingRange);

        if (targets.isEmpty()) {
            return;
        }

        this.notifyObservers();
    }

    private void notifyObservers() {
        for (TriggerObserver observer : observers) {
            observer.onTrigger();
        }
    }
}
