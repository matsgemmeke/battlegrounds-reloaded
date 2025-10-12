package nl.matsgemmeke.battlegrounds.item.trigger.enemy;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;

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
        TriggerTarget target = context.target();

        if (!target.exists()) {
            return false;
        }

        List<GameEntity> targets = targetFinder.findEnemyTargets(context.entity().getUniqueId(), target.getLocation(), checkingRange);
        return !targets.isEmpty();
    }
}
