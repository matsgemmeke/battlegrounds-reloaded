package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;

public record Damage(double amount, DamageType type, HitboxComponentType hitboxComponentType) {
}
