package nl.matsgemmeke.battlegrounds.entity.hitbox;

/**
 * A single box component of a hitbox.
 */
public record HitboxComponent(
        HitboxComponentType type,
        double height,
        double width,
        double depth,
        double offsetX,
        double offsetY,
        double offsetZ
) {
}
