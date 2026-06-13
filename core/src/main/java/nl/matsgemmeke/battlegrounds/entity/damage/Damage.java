package nl.matsgemmeke.battlegrounds.entity.damage;

import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;

public record Damage(double amount, DamageType type, HitboxComponentType hitboxComponentType) {
}
