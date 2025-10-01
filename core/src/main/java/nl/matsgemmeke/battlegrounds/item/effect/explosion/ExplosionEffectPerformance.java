package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExplosionEffectPerformance implements ItemEffectPerformance {

    private final DamageProcessor damageProcessor;
    private final ExplosionProperties properties;
    private final Set<TriggerRun> triggerRuns;
    private final TargetFinder targetFinder;

    @Inject
    public ExplosionEffectPerformance(DamageProcessor damageProcessor, TargetFinder targetFinder, @Assisted ExplosionProperties properties) {
        this.damageProcessor = damageProcessor;
        this.targetFinder = targetFinder;
        this.properties = properties;
        this.triggerRuns = new HashSet<>();
    }

    @Override
    public void addTriggerRun(TriggerRun triggerRun) {
        triggerRuns.add(triggerRun);
    }

    @Override
    public boolean isPerforming() {
        // An explosion effect is instant, therefore this effect will never perform for a longer period of time
        return false;
    }

    @Override
    public void perform(ItemEffectContext context) {
        Entity entity = context.getEntity();
        UUID entityId = entity.getUniqueId();
        ItemEffectSource source = context.getSource();
        Location sourceLocation = source.getLocation();
        World world = source.getWorld();

        double range = properties.rangeProfile().longRangeDistance();

        for (GameEntity target : targetFinder.findTargets(entityId, sourceLocation, range)) {
            Location targetLocation = target.getLocation();
            Damage damage = this.getDamageForTargetLocation(sourceLocation, targetLocation);

            target.damage(damage);
        }

        for (DeploymentObject deploymentObject : targetFinder.findDeploymentObjects(entityId, sourceLocation, range)) {
            if (deploymentObject != source) {
                Location objectLocation = deploymentObject.getLocation();
                Damage damage = this.getDamageForTargetLocation(sourceLocation, objectLocation);

                damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);
            }
        }

        // Remove the source before creating the explosion to prevent calling an extra EntityDamageByEntityEvent
        source.remove();

        world.createExplosion(sourceLocation, properties.power(), properties.setFire(), properties.breakBlocks(), entity);
    }

    private Damage getDamageForTargetLocation(Location sourceLocation, Location targetLocation) {
        double distance = sourceLocation.distance(targetLocation);
        double damageAmount = properties.rangeProfile().getDamageByDistance(distance);

        return new Damage(damageAmount, DamageType.EXPLOSIVE_DAMAGE);
    }

    @Override
    public void cancel() {
        triggerRuns.forEach(TriggerRun::cancel);
    }
}
