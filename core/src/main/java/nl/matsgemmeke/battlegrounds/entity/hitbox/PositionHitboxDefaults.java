package nl.matsgemmeke.battlegrounds.entity.hitbox;

import java.util.Set;

public final class PositionHitboxDefaults {

    public static final PositionHitbox DEFAULT_PLAYER_STANDING_HITBOX = new PositionHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.4, 0.4, 0.4, 0.0, 1.4, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.7, 0.4, 0.2, 0.0, 0.7, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.7, 0.4, 0.2, 0.0, 0.0, 0.0)
    ));

    public static final PositionHitbox DEFAULT_ZOMBIE_STANDING_HITBOX = new PositionHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.HEAD, 0.4, 0.4, 0.4, 0.0, 1.6, 0.0),
            new HitboxComponent(HitboxComponentType.TORSO, 0.8, 0.4, 0.2, 0.0, 0.8, 0.0),
            new HitboxComponent(HitboxComponentType.LIMBS, 0.8, 0.4, 0.2, 0.0, 0.0, 0.0)
    ));
}
