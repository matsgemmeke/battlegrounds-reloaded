package nl.matsgemmeke.battlegrounds.game.component.targeting.area;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;

/**
 * A target area implementation that selects targets whose hitboxes intersect a single point in space.
 */
public class PointTargetArea implements TargetArea {

    @Override
    public boolean contains(DamageTarget target, Location origin) {
        return target.getHitbox().intersects(origin);
    }
}
