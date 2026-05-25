package nl.matsgemmeke.battlegrounds.game.event;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageEventHandler implements EventHandler<EntityDamageEvent> {

    private final DeploymentObjectRegistry deploymentObjectRegistry;

    @Inject
    public EntityDamageEventHandler(DeploymentObjectRegistry deploymentObjectRegistry) {
        this.deploymentObjectRegistry = deploymentObjectRegistry;
    }

    public void handle(EntityDamageEvent event) {
        for (DamageTarget damageTarget : deploymentObjectRegistry.getDamageableDeploymentObjects()) {
            if (damageTarget.getUniqueId().equals(event.getEntity().getUniqueId())) {
                Damage damage = new Damage(event.getDamage(), DamageType.ENVIRONMENTAL_DAMAGE, HitboxComponentType.TORSO);

                damageTarget.damage(damage);
            }
        }
    }
}
