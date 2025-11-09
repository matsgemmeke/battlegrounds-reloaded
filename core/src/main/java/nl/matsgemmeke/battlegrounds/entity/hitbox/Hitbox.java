package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;

import java.util.Optional;

public interface Hitbox {

    PositionHitbox getCurrentPositionHitbox();

    Optional<HitboxComponentType> getHitboxComponentType(Location location);

    boolean intersects(Location location);
}
