package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;

public final class HitboxUtil {

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
