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

public class OpenModeDamageProcessor implements DamageProcessor {

    @NotNull
    private final DeploymentInfoProvider deploymentInfoProvider;
    @NotNull
    private final GameKey gameKey;
    @NotNull
    private final List<DamageCheck> damageChecks;

    public OpenModeDamageProcessor(@NotNull GameKey gameKey, @NotNull DeploymentInfoProvider deploymentInfoProvider) {
        this.gameKey = gameKey;
        this.deploymentInfoProvider = deploymentInfoProvider;
        this.damageChecks = new ArrayList<>();
    }

    public void addDamageCheck(@NotNull DamageCheck damageCheck) {
        damageChecks.add(damageCheck);
    }

    public boolean isDamageAllowed(@Nullable GameKey gameKey) {
        // Damage in open mode is allowed if both entities are in open mode, or if one of the entities has no game key,
        // meaning it's a normal open world entity
        return gameKey == null || gameKey == this.gameKey;
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
            DeployableItem deployableItem = deploymentInfoProvider.getDeployableItem(deploymentObject);

            if (deployableItem != null) {
                deployableItem.destroyDeployment();
            }
        }
    }
}
