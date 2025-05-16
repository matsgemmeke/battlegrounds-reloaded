package nl.matsgemmeke.battlegrounds.item.trigger.enemy;

import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import org.jetbrains.annotations.NotNull;

public interface EnemyProximityTriggerFactory {

    @NotNull
    Trigger create(@NotNull TargetFinder targetFinder, double checkingRange, long periodBetweenChecks);
}
