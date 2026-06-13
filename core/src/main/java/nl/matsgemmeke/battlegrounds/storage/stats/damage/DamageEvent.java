package nl.matsgemmeke.battlegrounds.storage.stats.damage;

import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.game.GameKey;

import java.time.Instant;
import java.util.UUID;

public record DamageEvent(
        GameKey gameKey,
        UUID sourceId,
        EntityKey sourceEntityKey,
        UUID targetId,
        EntityKey targetEntityType,
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
