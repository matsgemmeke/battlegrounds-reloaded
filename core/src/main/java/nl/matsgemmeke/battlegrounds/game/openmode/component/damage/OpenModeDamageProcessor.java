package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.game.damage.modifier.DamageModifier;

import java.util.ArrayList;
import java.util.List;

public class OpenModeDamageProcessor implements DamageProcessor {

    private final GameKey gameKey;
    private final List<DamageModifier> damageModifiers;

    @Inject
    public OpenModeDamageProcessor(GameKey gameKey) {
        this.gameKey = gameKey;
        this.damageModifiers = new ArrayList<>();
    }

    public void addDamageModifier(DamageModifier damageModifier) {
        damageModifiers.add(damageModifier);
    }

    public boolean isDamageAllowed(GameKey gameKey) {
        // Damage in open mode is allowed if both entities are in open mode
        return gameKey == this.gameKey;
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

        System.out.println("processing damage " + damageContext.damage());

        DamageTarget target = damageContext.target();
        Damage damage = damageContext.damage();

        target.damage(damage);
    }
}
