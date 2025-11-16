package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;

import java.util.Optional;
import java.util.Set;

/**
 * A hitbox for a specific entity position.
 */
public class PositionHitbox {

    private final Location baseLocation;
    private final RelativeHitbox relativeHitbox;

    public PositionHitbox(Location baseLocation, RelativeHitbox relativeHitbox) {
        this.baseLocation = baseLocation;
        this.relativeHitbox = relativeHitbox;
    }

    public Set<HitboxComponent> getComponents() {
        return relativeHitbox.components();
    }

    /**
     * Gets the intersected {@link HitboxComponent} of a given location.
     *
     * @param location    the location
     * @return            an optional containing the intersected hitbox component or empty when no components are
     *                    intersected by the given location
     */
    public Optional<HitboxComponent> getIntersectedHitboxComponent(Location location) {
        for (HitboxComponent component : relativeHitbox.components()) {
            if (intersectsHitboxComponent(location, baseLocation, component)) {
                return Optional.of(component);
            }
        }

        return Optional.empty();
    }

    /**
     * Gets whether a location intersects with given box properties.
     *
     * @param location    the location
     * @return            whether the given location intersets the given box properties
     */
    public boolean intersects(Location location) {
        for (HitboxComponent component : relativeHitbox.components()) {
            if (intersectsHitboxComponent(location, baseLocation, component)) {
                return true;
            }
        }

        return false;
    }

    private boolean intersectsHitboxComponent(Location location, Location boxLocation, HitboxComponent component) {
        double height = component.height();
        double halfWidth = component.width() / 2;
        double halfDepth = component.depth() / 2;
        double offsetX = component.offsetX();
        double offsetY = component.offsetY();
        double offsetZ = component.offsetZ();

        double difX = location.getX() - boxLocation.getX();
        double difY = location.getY() - boxLocation.getY();
        double difZ = location.getZ() - boxLocation.getZ();

        double yaw = Math.toRadians(boxLocation.getYaw());

        double rotatedX = difX * Math.cos(-yaw) - difZ * Math.sin(-yaw);
        double rotatedZ = difX * Math.sin(-yaw) + difZ * Math.cos(-yaw);

        rotatedX -= offsetX;
        difY -= offsetY;
        rotatedZ -= offsetZ;

        return rotatedX >= -halfWidth && rotatedX <= halfWidth
                && difY >= 0 && difY <= height
                && rotatedZ >= -halfDepth && rotatedZ <= halfDepth;
    }
}
