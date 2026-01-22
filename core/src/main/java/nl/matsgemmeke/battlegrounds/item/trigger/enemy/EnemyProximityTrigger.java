package nl.matsgemmeke.battlegrounds.item.trigger.enemy;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;

import java.util.List;

public class EnemyProximityTrigger implements Trigger {

    private final TargetFinder targetFinder;
    private double checkingRange;

    @Inject
    public EnemyProximityTrigger(TargetFinder targetFinder) {
        this.targetFinder = targetFinder;
    }

    public void setCheckingRange(double checkingRange) {
        this.checkingRange = checkingRange;
    }

    @Override
    public boolean activates(TriggerContext context) {
        return false;
    }

    @Override
    public TriggerResult check(TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        List<GameEntity> targets = targetFinder.findEnemyTargets(context.sourceId(), target.getLocation(), checkingRange);

        if (targets.isEmpty()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        return SimpleTriggerResult.ACTIVATES;
    }
}
