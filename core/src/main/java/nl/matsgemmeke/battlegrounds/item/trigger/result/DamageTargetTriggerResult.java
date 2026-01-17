package nl.matsgemmeke.battlegrounds.item.trigger.result;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;

/**
 * A trigger result that carries data of an entity that was hit.
 */
public class DamageTargetTriggerResult implements TriggerResult {

    private final DamageTarget damageTarget;
    private final Location hitLocation;

    public DamageTargetTriggerResult(DamageTarget damageTarget, Location hitLocation) {
        this.damageTarget = damageTarget;
        this.hitLocation = hitLocation;
    }

    @Override
    public boolean activates() {
        return true;
    }

    public DamageTarget getDamageTarget() {
        return damageTarget;
    }

    public Location getHitLocation() {
        return hitLocation;
    }
}
