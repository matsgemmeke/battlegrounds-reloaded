package nl.matsgemmeke.battlegrounds.storage.stats.damage;

import java.time.Instant;
import java.util.UUID;

public record DamageEvent(
        String gameKey,
        UUID damagerId,
        UUID victimId,
        String item,
        double damageAmount,
        String damageType,
        String hitbox,
        double distance,
        boolean kill,
        boolean friendlyFire,
        Instant timestamp
) {
}
