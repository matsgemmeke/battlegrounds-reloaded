package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.modifier.DamageModifier;
import nl.matsgemmeke.battlegrounds.item.deploy.DeployableItem;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OpenModeDamageProcessor implements DamageProcessor {

    private final DeploymentInfoProvider deploymentInfoProvider;
    private final GameKey gameKey;
    private final List<DamageModifier> damageModifiers;

    @Inject
    public OpenModeDamageProcessor(GameKey gameKey, DeploymentInfoProvider deploymentInfoProvider) {
        this.gameKey = gameKey;
        this.deploymentInfoProvider = deploymentInfoProvider;
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

    @NotNull
    public DamageEvent processDamage(@NotNull DamageEvent event) {
        for (DamageModifier damageModifier : damageModifiers) {
            damageModifier.apply(event);
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
