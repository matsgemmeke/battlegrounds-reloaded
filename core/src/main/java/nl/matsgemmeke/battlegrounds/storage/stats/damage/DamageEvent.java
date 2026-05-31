package nl.matsgemmeke.battlegrounds.storage.stats.damage;

import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;

import java.time.Instant;
import java.util.UUID;

public record DamageEvent(
        GameKey gameKey,
        UUID damagerId,
        EntityKey damagerEntityKey,
        UUID victimId,
        EntityKey victimEntityType,
        String item,
        double damageAmount,
        DamageType damageType,
        HitboxComponentType hitboxComponentType,
        double distance,
        boolean kill,
        boolean friendlyFire,
        Instant timestamp
) {
}
