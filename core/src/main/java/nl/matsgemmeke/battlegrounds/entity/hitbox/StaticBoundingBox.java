package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;

public record StaticBoundingBox(Location baseLocation, double widthX, double height, double widthZ) {
}
