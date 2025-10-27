package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;

public final class HitboxUtil {

    /**
     * Gets whether a location intersects with given box properties.
     *
     * @param location    the location
     * @param boxLocation the center location of the box
     * @param height      the box height
     * @param width       the box width
     * @return            whether the given location intersets the given box properties
     */
    public static boolean intersectsBox(Location location, Location boxLocation, double height, double width) {
        double halfWidth = width / 2;

        double minX = boxLocation.getX() - halfWidth;
        double maxX = boxLocation.getX() + halfWidth;
        double minY = boxLocation.getY();
        double maxY = boxLocation.getY() + height;
        double minZ = boxLocation.getZ() - halfWidth;
        double maxZ = boxLocation.getZ() + halfWidth;

        double locX = location.getX();
        double locY = location.getY();
        double locZ = location.getZ();

        return locX >= minX && locX <= maxX
                && locY >= minY && locY <= maxY
                && locZ >= minZ && locZ <= maxZ;
    }
}
