package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;

import java.util.Optional;

public interface Hitbox {

    Optional<HitboxPart> getHitPart(Location location);

    boolean intersects(Location location);
}
