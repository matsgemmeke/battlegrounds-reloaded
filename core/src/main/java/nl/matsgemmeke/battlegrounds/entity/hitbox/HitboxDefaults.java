package nl.matsgemmeke.battlegrounds.entity.hitbox;

import java.util.Set;

public final class HitboxDefaults {

    public static final RelativeHitbox PLAYER_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.4, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.7, 0.5, 0.3, 0.0, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.2, 0.3, 0.35, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.2, 0.3, -0.35, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.5, 0.3, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox PLAYER_SNEAKING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.0, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.5, 0.5, 0.0, 0.5, -0.2),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.2, 0.5, 0.35, 0.4, -0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.2, 0.5, -0.35, 0.4, -0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.5, 0.3, 0.0, 0.0, -0.2)
    ));

    public static final RelativeHitbox PLAYER_SLEEPING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, -0.3, -0.1),
            new HitboxComponent(HitboxComponentType.TORSO, 0.3, 0.5, 0.7, 0.0, -0.3, 0.4),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 0.2, 0.7, 0.35, -0.3, 0.4),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 0.2, 0.7, -0.35, -0.3, 0.4),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 0.5, 0.7, 0.0, -0.3, 1.1)
    ));

    public static final RelativeHitbox SKELETON_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.5, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.7, 0.5, 0.3, 0.0, 0.8, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.25, 0.25, 0.8, 0.35, 1.2, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.25, 0.25, 0.8, -0.35, 1.2, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.5, 0.2, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox SPIDER_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 0.3, 0.45),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.6, 1.1, 0.0, 0.3, -0.4),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.7, 1.6, 0.65, 0.0, -0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.7, 1.6, -0.65, 0.0, -0.1)
    ));

    public static final RelativeHitbox ZOMBIE_ADULT_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.5, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.7, 0.5, 0.3, 0.0, 0.8, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.25, 0.25, 0.8, 0.35, 1.2, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.25, 0.25, 0.8, -0.35, 1.2, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.5, 0.2, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox ZOMBIE_BABY_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.4, 0.4, 0.4, 0.0, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.35, 0.3, 0.2, 0.0, 0.35, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.15, 0.15, 0.4, 0.15, 0.55, 0.15),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.15, 0.15, 0.4, -0.15, 0.55, 0.15),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.35, 0.3, 0.2, 0.0, 0.0, 0.0)
    ));
}
