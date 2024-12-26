package nl.matsgemmeke.battlegrounds.game.training.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.info.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.check.DamageCheck;
import nl.matsgemmeke.battlegrounds.item.deploy.DeployableItem;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TrainingModeDamageProcessor implements DamageProcessor {

    @NotNull
    private DeploymentInfoProvider deploymentInfoProvider;
    @NotNull
    private List<DamageCheck> damageChecks;
    @NotNull
    private GameContext trainingModeContext;

    public TrainingModeDamageProcessor(@NotNull GameContext trainingModeContext, @NotNull DeploymentInfoProvider deploymentInfoProvider) {
        this.trainingModeContext = trainingModeContext;
        this.deploymentInfoProvider = deploymentInfoProvider;
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

    public void processDeploymentObjectDamage(@NotNull DeploymentObject deploymentObject, double damageAmount) {
        double remainingHealth = deploymentObject.damage(damageAmount);

        if (remainingHealth <= 0.0) {
            deploymentObject.destroy();

            DeployableItem deployableItem = deploymentInfoProvider.getDeployableItem(deploymentObject);

            if (deployableItem != null) {
                deployableItem.onDestroyDeploymentObject(deploymentObject);
            }
        }
    }
}
