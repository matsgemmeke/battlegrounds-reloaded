package nl.matsgemmeke.battlegrounds.item.trigger.result;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;

/**
 * A trigger result that carries data of an entity that was hit.
 */
public class DamageTargetTriggerResult implements TriggerResult {

    private final DamageTarget hitTarget;
    private final Location hitLocation;

    public DamageTargetTriggerResult(DamageTarget hitTarget, Location hitLocation) {
        this.hitTarget = hitTarget;
        this.hitLocation = hitLocation;
    }

    @Override
    public boolean activates() {
        return true;
    }

    public DamageTarget getHitTarget() {
        return hitTarget;
    }

    public Location getHitLocation() {
        return hitLocation;
    }
}
