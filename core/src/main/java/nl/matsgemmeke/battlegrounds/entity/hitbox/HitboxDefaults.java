package nl.matsgemmeke.battlegrounds.entity.hitbox;

import java.util.Set;

public final class HitboxDefaults {

    public static final RelativeHitbox PLAYER_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.4, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.7, 0.5, 0.3, 0.0, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.2, 0.3, -0.35, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.2, 0.3, 0.35, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.5, 0.3, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox ZOMBIE_ADULT_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.5, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.7, 0.5, 0.3, 0.0, 0.8, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.25, 0.25, 0.8, -0.35, 1.2, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.25, 0.25, 0.8, 0.35, 1.2, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.5, 0.2, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox ZOMBIE_BABY_STANDING = new RelativeHitbox(Set.of(
    ));
}
