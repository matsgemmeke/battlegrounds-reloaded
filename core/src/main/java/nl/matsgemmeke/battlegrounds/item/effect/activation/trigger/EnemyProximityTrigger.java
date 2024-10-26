package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
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

    public void checkTriggerActivation(@NotNull ItemHolder holder, @NotNull Deployable object) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(holder, object), RUNNABLE_DELAY, periodBetweenChecks);
    }

    private void runCheck(@NotNull ItemHolder holder, @NotNull Deployable object) {
        if (!object.exists()) {
            task.cancel();
            return;
        }

        List<GameEntity> targets = targetFinder.findEnemyTargets(holder, object.getLocation(), checkingRange);

        if (targets.isEmpty()) {
            return;
        }

        this.notifyObservers(holder, object);
        task.cancel();
    }

    private void notifyObservers(@NotNull ItemHolder holder, @NotNull Deployable object) {
        for (TriggerObserver observer : observers) {
            observer.onTrigger(holder, object);
        }
    }
}
