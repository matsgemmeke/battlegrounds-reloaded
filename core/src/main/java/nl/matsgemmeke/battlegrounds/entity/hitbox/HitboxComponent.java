package nl.matsgemmeke.battlegrounds.entity.hitbox;

/**
 * A single box component of a hitbox.
 */
public record HitboxComponent(
        HitboxComponentType type,
        double width,
        double height,
        double depth,
        double offsetX,
        double offsetY,
        double offsetZ
) {
}
