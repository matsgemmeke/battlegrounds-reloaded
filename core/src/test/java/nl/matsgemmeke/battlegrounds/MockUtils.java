package nl.matsgemmeke.battlegrounds;

import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitResult;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.bukkit.Location;
import org.mockito.stubbing.Answer;

public final class MockUtils {

    /**
     * Answer stub that immediately runs a schedule task when they are added to a schedule.
     */
    public static final Answer<Void> RUN_SCHEDULE_TASK = invocation -> {
        ScheduleTask task = invocation.getArgument(0);
        task.run();
        return null;
    };

    public static Answer<Void> answerNotifyTriggerObserver(TriggerResult triggerResult) {
        return invocation -> {
            TriggerObserver triggerObserver = invocation.getArgument(0);
            triggerObserver.onActivate(triggerResult);
            return null;
        };
    }

    public static Answer<Void> answerRunGameScopeRunnable() {
        return invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        };
    }

    public static Answer<Void> answerRunProjectileHitAction(Location hitLocation) {
        return invocation -> {
            ProjectileHitAction projectileHitAction = invocation.getArgument(1);
//            projectileHitAction.onProjectileHit(hitLocation);
            return null;
        };
    }

    public static Answer<Void> answerRunProjectileHitAction(ProjectileHitResult projectileHitResult) {
        return invocation -> {
            ProjectileHitAction projectileHitAction = invocation.getArgument(1);
            projectileHitAction.onProjectileHit(projectileHitResult);
            return null;
        };
    }

    public static Answer<Void> answerScheduleTaskRun(Number times) {
        return invocation -> {
            ScheduleTask task = invocation.getArgument(0);

            for (int i = 0; i < times.intValue(); i++) {
                task.run();
            }

            return null;
        };
    }
}
