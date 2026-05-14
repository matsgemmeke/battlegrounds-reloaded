package nl.matsgemmeke.battlegrounds.storage.stats.damage;

import java.time.Instant;
import java.util.UUID;

public record DamageEvent(
        UUID damagerId,
        UUID victimId,
        String item,
        double damageAmount,
        String hitbox,
        double distance,
        boolean kill,
        boolean friendlyFire,
        Instant timestamp
) {
}
