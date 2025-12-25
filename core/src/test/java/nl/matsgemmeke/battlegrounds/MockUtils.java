package nl.matsgemmeke.battlegrounds;

import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.mockito.stubbing.Answer;

public final class MockUtils {

    /**
     * Answer stub that immediately runs a projectile hit action callback when they are added into a registry.
     */
    public static final Answer<Void> RUN_PROJECTILE_HIT_ACTION = invocation -> {
        ProjectileHitAction projectileHitAction = invocation.getArgument(1);
        projectileHitAction.onProjectileHit();
        return null;
    };

    /**
     * Answer stub that immediately runs a schedule task when they are added to a schedule.
     */
    public static final Answer<Void> RUN_SCHEDULE_TASK = invocation -> {
        ScheduleTask task = invocation.getArgument(0);
        task.run();
        return null;
    };

    /**
     * Answer stub that immediately runs a trigger observer when they are added to a trigger run.
     */
    public static final Answer<Void> RUN_TRIGGER_OBSERVER = invocation -> {
        TriggerObserver triggerObserver = invocation.getArgument(0);
        triggerObserver.onActivate();
        return null;
    };

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
