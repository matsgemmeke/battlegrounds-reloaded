package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;

public class HumanoidHitbox implements Hitbox {

    @Override
    public boolean intersects(Location location) {
        return false;
    }
}
