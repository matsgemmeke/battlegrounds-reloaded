package nl.matsgemmeke.battlegrounds.entity.hitbox;

import java.util.Set;

/**
 * A hitbox for a specific entity position.
 */
public record PositionHitbox(Set<HitboxComponent> components) {
}
