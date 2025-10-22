package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;

public interface Hitbox {

    boolean intersects(Location location);
}
