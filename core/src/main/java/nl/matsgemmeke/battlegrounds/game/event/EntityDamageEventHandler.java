package nl.matsgemmeke.battlegrounds.game.event;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageEventHandler implements EventHandler<EntityDamageEvent> {

    private final DamageProcessor damageProcessor;
    private final DeploymentObjectRegistry deploymentObjectRegistry;

    @Inject
    public EntityDamageEventHandler(DamageProcessor damageProcessor, DeploymentObjectRegistry deploymentObjectRegistry) {
        this.damageProcessor = damageProcessor;
        this.deploymentObjectRegistry = deploymentObjectRegistry;
    }

    public void handle(EntityDamageEvent event) {
        for (DamageTarget damageTarget : deploymentObjectRegistry.getDamageableDeploymentObjects()) {
            if (damageTarget.getUniqueId().equals(event.getEntity().getUniqueId())) {
                Damage damage = new Damage(event.getDamage(), DamageType.ENVIRONMENTAL_DAMAGE);
                DamageContext damageContext = new DamageContext(null, damageTarget, damage);

                damageProcessor.processDamage(damageContext);
            }
        }
    }
}
