package nl.matsgemmeke.battlegrounds.entity.hitbox;

import java.util.Set;

public final class HitboxDefaults {

    public static final RelativeHitbox CHICKEN_ADULT_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.3, 0.4, 0.3, 0.0, 0.55, 0.3),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.4, 0.55, 0.0, 0.3, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.3, 0.2, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox CHICKEN_BABY_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.3, 0.4, 0.3, 0.0, 0.2, 0.2),
            new HitboxComponent(HitboxComponentType.TORSO, 0.2, 0.2, 0.3, 0.0, 0.1, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 0.1, 0.2, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox COW_ADULT_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.4, 0.0, 1.0, 0.7),
            new HitboxComponent(HitboxComponentType.TORSO, 0.8, 0.7, 1.2, 0.0, 0.7, -0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.7, 0.3, 0.0, 0.0, 0.35),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.7, 0.3, 0.0, 0.0, -0.45)
    ));

    public static final RelativeHitbox COW_BABY_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.4, 0.0, 0.35, 0.45),
            new HitboxComponent(HitboxComponentType.TORSO, 0.4, 0.3, 0.55, 0.0, 0.35, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.4, 0.35, 0.15, 0.0, 0.0, 0.2),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.4, 0.35, 0.15, 0.0, 0.0, -0.25)
    ));

    public static final RelativeHitbox CREEPER_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.1, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.7, 0.3, 0.0, 0.4, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.4, 0.8, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox ENDERMAN_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 2.3, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.7, 0.3, 0.0, 1.6, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 1.8, 0.3, 0.35, 0.5, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 1.8, 0.3, -0.35, 0.5, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 1.6, 0.3, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox ENDERMAN_CARRYING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 2.3, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.7, 0.3, 0.0, 1.6, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 1.6, 0.6, 0.35, 0.7, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 1.6, 0.6, -0.35, 0.7, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 1.6, 0.3, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox IRON_GOLEM_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.7, 0.6, 0.0, 2.0, 0.25),
            new HitboxComponent(HitboxComponentType.TORSO, 1.1, 1.0, 0.7, 0.0, 1.0, 0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 1.9, 0.4, 0.7, 0.2, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 1.9, 0.4, -0.7, 0.2, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 1.0, 1.0, 0.5, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox PIG_ADULT_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.6, 0.0, 0.5, 0.65),
            new HitboxComponent(HitboxComponentType.TORSO, 0.7, 0.5, 1.0, 0.0, 0.35, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.35, 0.3, 0.0, 0.0, 0.35),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.35, 0.3, 0.0, 0.0, -0.4)
    ));

    public static final RelativeHitbox PIG_BABY_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.6, 0.0, 0.25, 0.4),
            new HitboxComponent(HitboxComponentType.TORSO, 0.4, 0.3, 0.5, 0.0, 0.15, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.4, 0.15, 0.15, 0.0, 0.0, 0.15),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.4, 0.15, 0.15, 0.0, 0.0, -0.25)
    ));

    public static final RelativeHitbox PLAYER_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.4, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.7, 0.3, 0.0, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 0.7, 0.3, 0.35, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 0.7, 0.3, -0.35, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.7, 0.3, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox PLAYER_SNEAKING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.0, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.5, 0.5, 0.0, 0.5, -0.2),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 0.7, 0.5, 0.35, 0.4, -0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 0.7, 0.5, -0.35, 0.4, -0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.5, 0.3, 0.0, 0.0, -0.2)
    ));

    public static final RelativeHitbox PLAYER_SLEEPING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, -0.3, -0.1),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.3, 0.7, 0.0, -0.3, 0.4),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 0.3, 0.7, 0.35, -0.3, 0.4),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 0.3, 0.7, -0.35, -0.3, 0.4),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.3, 0.7, 0.0, -0.3, 1.1)
    ));

    public static final RelativeHitbox SHEEP_ADULT_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 0.9, 0.6),
            new HitboxComponent(HitboxComponentType.TORSO, 0.8, 0.6, 1.2, 0.0, 0.6, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.6, 0.3, 0.0, 0.0, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.6, 0.3, 0.0, 0.0, -0.45)
    ));

    public static final RelativeHitbox SHEEP_BABY_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 0.4, 0.35),
            new HitboxComponent(HitboxComponentType.TORSO, 0.4, 0.3, 0.65, 0.0, 0.3, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.4, 0.3, 0.15, 0.0, 0.0, 0.15),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.4, 0.3, 0.15, 0.0, 0.0, -0.25)
    ));

    public static final RelativeHitbox SKELETON_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.5, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.75, 0.3, 0.0, 0.75, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 0.75, 0.2, 0.35, 0.75, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.2, 0.75, 0.2, -0.35, 0.75, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.4, 0.75, 0.3, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox SLIME_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.5, 0.5, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox SPIDER_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 0.3, 0.45),
            new HitboxComponent(HitboxComponentType.TORSO, 0.6, 0.5, 1.1, 0.0, 0.3, -0.4),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.5, 1.6, 0.65, 0.0, -0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.5, 1.6, -0.65, 0.0, -0.1)
    ));

    public static final RelativeHitbox VILLAGER_ADULT_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.6, 0.5, 0.0, 1.4, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.6, 0.8, 0.4, 0.0, 0.6, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.9, 0.5, 0.4, 0.0, 0.9, 0.2),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.6, 0.6, 0.4, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox VILLAGER_ADULT_SLEEPING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.6, 0.0, -0.3, -0.2),
            new HitboxComponent(HitboxComponentType.TORSO, 0.6, 0.4, 0.8, 0.0, -0.2, 0.5),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.9, 0.4, 0.5, 0.0, -0.1, 0.4),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.6, 0.4, 0.6, 0.0, -0.2, 1.2)
    ));

    public static final RelativeHitbox VILLAGER_BABY_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.25, 0.3, 0.25, 0.0, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.3, 0.4, 0.2, 0.0, 0.3, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.45, 0.25, 0.2, 0.0, 0.45, 0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 0.3, 0.2, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox VILLAGER_BABY_SLEEPING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.25, 0.25, 0.3, 0.0, -0.15, -0.1),
            new HitboxComponent(HitboxComponentType.TORSO, 0.3, 0.2, 0.4, 0.0, -0.1, 0.25),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.45, 0.2, 0.25, 0.0, -0.05, 0.2),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 0.2, 0.3, 0.0, -0.1, 0.6)
    ));

    public static final RelativeHitbox WOLF_ADULT_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.4, 0.4, 0.5, 0.0, 0.45, 0.55),
            new HitboxComponent(HitboxComponentType.TORSO, 0.6, 0.5, 1.0, 0.0, 0.4, -0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.4, 0.2, 0.0, 0.0, 0.2),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.4, 0.2, 0.0, 0.0, -0.4)
    ));

    public static final RelativeHitbox WOLF_ADULT_SITTING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.4, 0.4, 0.5, 0.0, 0.45, 0.45),
            new HitboxComponent(HitboxComponentType.TORSO, 0.6, 0.8, 0.7, 0.0, 0.0, -0.1),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.6, 0.3, 0.2, 0.0, 0.0, 0.4)
    ));

    public static final RelativeHitbox WOLF_BABY_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.4, 0.4, 0.5, 0.0, 0.15, 0.45),
            new HitboxComponent(HitboxComponentType.TORSO, 0.3, 0.3, 0.5, 0.0, 0.15, -0.05),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 0.15, 0.15, 0.0, 0.0, 0.15),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 0.15, 0.15, 0.0, 0.0, -0.2)
    ));

    public static final RelativeHitbox WOLF_BABY_SITTING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.4, 0.4, 0.5, 0.0, 0.15, 0.4),
            new HitboxComponent(HitboxComponentType.TORSO, 0.3, 0.4, 0.4, 0.0, 0.0, -0.05),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 0.1, 0.1, 0.0, 0.0, 0.25)
    ));

    public static final RelativeHitbox ZOMBIE_ADULT_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.5, 0.5, 0.5, 0.0, 1.5, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.75, 0.3, 0.0, 0.75, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.25, 0.25, 0.75, 0.35, 1.2, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.25, 0.25, 0.75, -0.35, 1.2, 0.3),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.5, 0.75, 0.2, 0.0, 0.0, 0.0)
    ));

    public static final RelativeHitbox ZOMBIE_BABY_STANDING = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.4, 0.4, 0.4, 0.0, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.3, 0.35, 0.2, 0.0, 0.35, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.15, 0.15, 0.4, 0.15, 0.55, 0.15),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.15, 0.15, 0.4, -0.15, 0.55, 0.15),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.3, 0.35, 0.2, 0.0, 0.0, 0.0)
    ));
}
