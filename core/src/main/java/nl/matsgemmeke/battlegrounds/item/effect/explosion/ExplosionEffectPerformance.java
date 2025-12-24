package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.Removable;
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
        ItemEffectSource effectSource = context.getEffectSource();
        Location effectSourceLocation = effectSource.getLocation();
        World world = effectSource.getWorld();

        double range = properties.rangeProfile().longRangeDistance();

        for (GameEntity target : targetFinder.findTargets(uniqueId, effectSourceLocation, range)) {
            Location targetLocation = target.getLocation();
            Damage damage = this.getDamageForTargetLocation(effectSourceLocation, targetLocation);

            target.damage(damage);
        }

        for (DeploymentObject deploymentObject : targetFinder.findDeploymentObjects(uniqueId, effectSourceLocation, range)) {
            if (deploymentObject != effectSource) {
                Location objectLocation = deploymentObject.getLocation();
                Damage damage = this.getDamageForTargetLocation(effectSourceLocation, objectLocation);

                damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);
            }
        }

        // Remove the source before creating the explosion to prevent calling an extra EntityDamageByEntityEvent
        if (effectSource instanceof Removable removableSource) {
            removableSource.remove();
        }

        world.createExplosion(effectSourceLocation, properties.power(), properties.setFire(), properties.breakBlocks());
    }

    private Damage getDamageForTargetLocation(Location sourceLocation, Location targetLocation) {
        double distance = sourceLocation.distance(targetLocation);
        double damageAmount = properties.rangeProfile().getDamageByDistance(distance);

        return new Damage(damageAmount, DamageType.EXPLOSIVE_DAMAGE);
    }
}
