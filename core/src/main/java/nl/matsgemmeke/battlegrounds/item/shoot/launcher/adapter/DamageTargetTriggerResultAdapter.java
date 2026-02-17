package nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.DamageTargetTriggerResult;
import org.bukkit.Location;

public class DamageTargetTriggerResultAdapter implements TriggerResultAdapter<DamageTargetTriggerResult> {

    @Override
    public CollisionResult adapt(DamageTargetTriggerResult triggerResult) {
        DamageTarget hitTarget = triggerResult.getHitTarget();
        Location hitLocation = triggerResult.getHitLocation();

        return new CollisionResult(null, hitTarget, hitLocation);
    }
}
