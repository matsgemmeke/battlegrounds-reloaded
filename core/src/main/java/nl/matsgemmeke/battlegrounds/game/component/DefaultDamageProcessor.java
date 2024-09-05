package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.check.DamageCheck;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DefaultDamageProcessor implements DamageProcessor {

    @NotNull
    private List<DamageCheck> damageChecks;

    public DefaultDamageProcessor() {
        this.damageChecks = new ArrayList<>();
    }

    public void addDamageCheck(@NotNull DamageCheck damageCheck) {
        damageChecks.add(damageCheck);
    }

    @NotNull
    public DamageEvent processDamage(@NotNull DamageEvent event) {
        for (DamageCheck damageCheck : damageChecks) {
            damageCheck.process(event);
        }

        return event;
    }
}
