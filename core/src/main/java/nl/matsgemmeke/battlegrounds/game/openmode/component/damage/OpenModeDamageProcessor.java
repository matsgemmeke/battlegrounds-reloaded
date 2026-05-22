package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.game.damage.modifier.DamageModifier;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OpenModeDamageProcessor implements DamageProcessor {

    private final Clock clock;
    private final DamageEventTracker damageEventTracker;
    private final GameKey gameKey;
    private final List<DamageModifier> damageModifiers;

    @Inject
    public OpenModeDamageProcessor(GameKey gameKey, Clock clock, DamageEventTracker damageEventTracker) {
        this.gameKey = gameKey;
        this.clock = clock;
        this.damageEventTracker = damageEventTracker;
        this.damageModifiers = new ArrayList<>();
    }

    public void addDamageModifier(DamageModifier damageModifier) {
        damageModifiers.add(damageModifier);
    }

    public boolean isDamageAllowed(GameKey gameKey) {
        // Damage in open mode is allowed if both entities are in open mode
        return gameKey.equals(this.gameKey);
    }

    public boolean isDamageAllowedWithoutContext() {
        // Entities in open mode are always allowed to damage entities outside game contexts
        return true;
    }

    @Override
    public void processDamage(DamageContext damageContext) {
        for (DamageModifier damageModifier : damageModifiers) {
            damageContext = damageModifier.apply(damageContext);
        }

        DamageTarget target = damageContext.target();
        Damage damage = damageContext.damage();

        double finalDamageAmount = target.damage(damage);

        String gameKey = this.gameKey.toString();
        UUID damagerId = damageContext.source().getUniqueId();
        UUID victimId = damageContext.target().getUniqueId();
        // TODO: real value
        String item = "TestWeapon";
        String hitbox = damageContext.damage().hitboxComponentType().name();
        double distance = damageContext.distance();
        boolean kill = target.getHealth() <= 0;
        // TODO: real value
        boolean friendlyFire = false;
        Instant timestamp = Instant.now(clock);
        DamageEvent damageEvent = new DamageEvent(gameKey, damagerId, victimId, item, finalDamageAmount, hitbox, distance, kill, friendlyFire, timestamp);

        damageEventTracker.add(damageEvent);
    }
}
