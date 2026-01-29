package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class ExplosionEffectPerformance extends BaseItemEffectPerformance {

    private final DamageProcessor damageProcessor;
    private final ExplosionProperties properties;
    private final TargetFinder targetFinder;

    @Inject
    public ExplosionEffectPerformance(DamageProcessor damageProcessor, TargetFinder targetFinder, @Assisted ExplosionProperties properties) {
        this.damageProcessor = damageProcessor;
        this.targetFinder = targetFinder;
        this.properties = properties;
    }

    @Override
    public boolean isPerforming() {
        // An explosion effect is instant, therefore this effect will never perform for a longer period of time
        return false;
    }

    @Override
    public void perform(ItemEffectContext context) {
        UUID uniqueId = context.getDamageSource().getUniqueId();
        Actor actor = context.getActor();
        Location actorLocation = actor.getLocation();
        World world = actor.getWorld();

        double range = properties.rangeProfile().longRangeDistance();

        for (GameEntity target : targetFinder.findTargets(uniqueId, actorLocation, range)) {
            Location targetLocation = target.getLocation();
            Damage damage = this.getDamageForTargetLocation(actorLocation, targetLocation);

            target.damage(damage);
        }

        for (DeploymentObject deploymentObject : targetFinder.findDeploymentObjects(uniqueId, actorLocation, range)) {
            if (deploymentObject != actor) {
                Location objectLocation = deploymentObject.getLocation();
                Damage damage = this.getDamageForTargetLocation(actorLocation, objectLocation);

                damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);
            }
        }

        // Remove the source before creating the explosion to prevent calling an extra EntityDamageByEntityEvent
        if (actor instanceof Removable removableActor) {
            removableActor.remove();
        }

        world.createExplosion(actorLocation, properties.power(), properties.setFire(), properties.breakBlocks());
    }

    private Damage getDamageForTargetLocation(Location sourceLocation, Location targetLocation) {
        double distance = sourceLocation.distance(targetLocation);
        double damageAmount = properties.rangeProfile().getDamageByDistance(distance);

        return new Damage(damageAmount, DamageType.EXPLOSIVE_DAMAGE);
    }
}
