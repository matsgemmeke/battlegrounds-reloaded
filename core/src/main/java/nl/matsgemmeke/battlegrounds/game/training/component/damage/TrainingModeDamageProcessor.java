package nl.matsgemmeke.battlegrounds.game.training.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
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
    private GameKey trainingModeKey;
    @NotNull
    private List<DamageCheck> damageChecks;

    public TrainingModeDamageProcessor(@NotNull GameKey trainingModeKey, @NotNull DeploymentInfoProvider deploymentInfoProvider) {
        this.trainingModeKey = trainingModeKey;
        this.deploymentInfoProvider = deploymentInfoProvider;
        this.damageChecks = new ArrayList<>();
    }

    public void addDamageCheck(@NotNull DamageCheck damageCheck) {
        damageChecks.add(damageCheck);
    }

    public boolean isDamageAllowed(@Nullable GameKey gameKey) {
        // Damage in training mode is allowed if both entities are in training mode, or if one of the entities has no
        // game key, meaning it's a normal open world entity
        return trainingModeKey == gameKey || gameKey == null;
    }

    @NotNull
    public DamageEvent processDamage(@NotNull DamageEvent event) {
        for (DamageCheck damageCheck : damageChecks) {
            damageCheck.process(event);
        }

        return event;
    }

    public void processDeploymentObjectDamage(@NotNull DeploymentObject deploymentObject, @NotNull Damage damage) {
        if (deploymentObject.isImmuneTo(damage.type())) {
            return;
        }

        deploymentObject.damage(damage);

        if (deploymentObject.getHealth() <= 0.0) {
            deploymentObject.remove();

            DeployableItem deployableItem = deploymentInfoProvider.getDeployableItem(deploymentObject);

            if (deployableItem != null) {
                deployableItem.onDestroyDeploymentObject(deploymentObject);
            }
        }
    }
}
