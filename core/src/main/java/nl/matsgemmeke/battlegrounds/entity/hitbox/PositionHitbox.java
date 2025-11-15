package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;

import java.util.Optional;
import java.util.Set;

/**
 * A hitbox for a specific entity position.
 */
public record PositionHitbox(Set<HitboxComponent> components) {

    public Optional<HitboxComponentType> getIntersectedHitboxComponentType(Location location) {
        return Optional.empty();
    }

    public boolean intersects(Location location) {
        return false;
    }
}
