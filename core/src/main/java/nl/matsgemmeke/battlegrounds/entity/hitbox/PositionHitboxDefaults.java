package nl.matsgemmeke.battlegrounds.entity.hitbox;

import java.util.Set;

public final class PositionHitboxDefaults {

    public static final PositionHitbox PLAYER_STANDING = new PositionHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.4, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.7, 0.5, 0.3, 0.0, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.2, 0.3, -0.35, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.2, 0.3, 0.35, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.5, 0.3, 0.0, 0.0, 0.0)
    ));

    public static final PositionHitbox ZOMBIE_ADULT_STANDING = new PositionHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.5, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.7, 0.5, 0.3, 0.0, 0.8, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.25, 0.25, 0.8, -0.35, 1.2, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.25, 0.25, 0.8, 0.35, 1.2, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.5, 0.2, 0.0, 0.0, 0.0)
    ));

    public static final PositionHitbox ZOMBIE_BABY_STANDING = new PositionHitbox(Set.of(
    ));
}
