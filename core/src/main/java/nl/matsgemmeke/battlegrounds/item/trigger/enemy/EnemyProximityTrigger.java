package nl.matsgemmeke.battlegrounds.item.trigger.enemy;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;

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
    public TriggerResult check(TriggerContext context) {
        Actor actor = context.actor();

        if (!actor.exists()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        List<GameEntity> targets = targetFinder.findEnemyTargets(context.sourceId(), actor.getLocation(), checkingRange);

        if (targets.isEmpty()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        return SimpleTriggerResult.ACTIVATES;
    }
}
