package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;

import java.util.Optional;

public final class HitboxUtil {

    /**
     * Gets the intersected {@link HitboxComponent} of a given location.
     *
     * @param location    the location
     * @param boxLocation the center location of the box
     * @param hitbox      the hitbox of the entity's current position
     * @return            an optional containing the intersected hitbox component or empty when no components are
     *                    intersected by the given location
     */
    public static Optional<HitboxComponent> getIntersectedHitboxComponent(Location location, Location boxLocation, PositionHitbox hitbox) {
        for (HitboxComponent component : hitbox.components()) {
            System.out.println("checking component " + component.type());
            if (intersectsHitboxComponent(location, boxLocation, component)) {
                return Optional.of(component);
            }
        }

        return Optional.empty();
    }

    /**
     * Gets whether a location intersects with given box properties.
     *
     * @param location    the location
     * @param boxLocation the center location of the box
     * @param hitbox      the hitbox of the entity's current position
     * @return            whether the given location intersets the given box properties
     */
    public static boolean intersectsHitbox(Location location, Location boxLocation, PositionHitbox hitbox) {
        for (HitboxComponent component : hitbox.components()) {
            if (intersectsHitboxComponent(location, boxLocation, component)) {
                return true;
            }
        }

        return false;
    }

    private static boolean intersectsHitboxComponent(Location location, Location boxLocation, HitboxComponent component) {
        double height = component.height();
        double halfWidth = component.width() / 2;
        double offsetX = component.offsetX();
        double offsetY = component.offsetY();
        double offsetZ = component.offsetZ();

        double minX = boxLocation.getX() - halfWidth + offsetX;
        double maxX = boxLocation.getX() + halfWidth + offsetX;
        double minY = boxLocation.getY() + offsetY;
        double maxY = boxLocation.getY() + height + offsetY;
        double minZ = boxLocation.getZ() - halfWidth + offsetZ;
        double maxZ = boxLocation.getZ() + halfWidth + offsetZ;

        double locX = location.getX();
        double locY = location.getY();
        double locZ = location.getZ();

        return locX >= minX && locX <= maxX
                && locY >= minY && locY <= maxY
                && locZ >= minZ && locZ <= maxZ;
    }
}
