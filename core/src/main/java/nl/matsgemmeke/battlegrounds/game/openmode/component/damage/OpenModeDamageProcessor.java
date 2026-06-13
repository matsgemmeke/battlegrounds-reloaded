package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
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
        DamageTarget target = damageContext.target();

        if (target.getHealth() <= 0) {
            return;
        }

        for (DamageModifier damageModifier : damageModifiers) {
            damageContext = damageModifier.apply(damageContext);
        }

        double finalDamageAmount = target.damage(damageContext.damage());

        UUID damagerId = damageContext.source().getUniqueId();
        EntityKey damagerEntityKey = damageContext.source().getEntityKey();
        UUID victimId = damageContext.target().getUniqueId();
        EntityKey victimEntityKey = damageContext.target().getEntityKey();
        String item = damageContext.itemName();
        DamageType damageType = damageContext.damage().type();
        HitboxComponentType hitboxComponentType = damageContext.damage().hitboxComponentType();
        double distance = damageContext.distance();
        boolean kill = target.getHealth() <= 0;
        boolean friendlyFire = false;
        Instant timestamp = Instant.now(clock);
        DamageEvent damageEvent = new DamageEvent(gameKey, damagerId, damagerEntityKey, victimId, victimEntityKey, item, finalDamageAmount, damageType, hitboxComponentType, distance, kill, friendlyFire, timestamp);

        damageEventTracker.add(damageEvent);
    }
}
