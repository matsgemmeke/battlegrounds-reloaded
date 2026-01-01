package nl.matsgemmeke.battlegrounds.game.component.targeting.area;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;

/**
 * A target area implementation that selects targets that are located inside the radius of a given location.
 */
public class RadiusTargetArea implements TargetArea {

    private final double radiusSquared;

    public RadiusTargetArea(double radius) {
        this.radiusSquared = radius * radius;
    }

    @Override
    public boolean contains(DamageTarget target, Location origin) {
        return target.getLocation().distanceSquared(origin) <= radiusSquared;
    }
}
