package nl.matsgemmeke.battlegrounds.game.event;

import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDamageEventHandler implements EventHandler<EntityDamageEvent> {

    @NotNull
    private DamageProcessor damageProcessor;
    @NotNull
    private DeploymentInfoProvider deploymentInfoProvider;

    public EntityDamageEventHandler(@NotNull DamageProcessor damageProcessor, @NotNull DeploymentInfoProvider deploymentInfoProvider) {
        this.damageProcessor = damageProcessor;
        this.deploymentInfoProvider = deploymentInfoProvider;
    }

    public void handle(@NotNull EntityDamageEvent event) {
        for (DeploymentObject deploymentObject : deploymentInfoProvider.getAllDeploymentObjects()) {
            if (deploymentObject.matchesEntity(event.getEntity())) {
                Damage damage = new Damage(event.getDamage(), DamageType.ENVIRONMENTAL_DAMAGE);

                damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);
            }
        }
    }
}
