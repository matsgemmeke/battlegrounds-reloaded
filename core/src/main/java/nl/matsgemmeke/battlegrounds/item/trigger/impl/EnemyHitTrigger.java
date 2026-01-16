package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import jakarta.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.CheckResult;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;

import java.util.Optional;
import java.util.UUID;

/**
 * A trigger that activates when the trigger target hits an enemy entity, by being intersecting the entity's hitbox.
 */
public class EnemyHitTrigger implements Trigger {

    private static final double TARGET_FINDING_RANGE = 0.1;

    private final TargetFinder targetFinder;

    @Inject
    public EnemyHitTrigger(TargetFinder targetFinder) {
        this.targetFinder = targetFinder;
    }

    @Override
    public boolean activates(TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            return false;
        }

        UUID sourceId = context.sourceId();
        Location targetLocation = target.getLocation();

        return !targetFinder.findEnemyTargets(sourceId, targetLocation, TARGET_FINDING_RANGE).stream()
                .map(GameEntity::getHitbox)
                .filter(hitbox -> hitbox.intersects(targetLocation))
                .toList()
                .isEmpty();
    }

    @Override
    public Optional<CheckResult> check(TriggerContext context) {
        return Optional.empty();
    }
}
