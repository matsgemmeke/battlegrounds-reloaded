package nl.matsgemmeke.battlegrounds.game.training.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.check.DamageCheck;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TrainingModeDamageProcessor implements DamageProcessor {

    @NotNull
    private List<DamageCheck> damageChecks;
    @NotNull
    private GameContext trainingModeContext;

    public TrainingModeDamageProcessor(@NotNull GameContext trainingModeContext) {
        this.trainingModeContext = trainingModeContext;
        this.damageChecks = new ArrayList<>();
    }

    public void addDamageCheck(@NotNull DamageCheck damageCheck) {
        damageChecks.add(damageCheck);
    }

    public boolean isDamageAllowed(@Nullable GameContext context) {
        // Damage in training mode is allowed if both entities are in the same training mode context, or if one of the
        // entities has no context, meaning it's a normal open world entity
        return trainingModeContext == context || context == null;
    }

    @NotNull
    public DamageEvent processDamage(@NotNull DamageEvent event) {
        for (DamageCheck damageCheck : damageChecks) {
            damageCheck.process(event);
        }

        return event;
    }
}
