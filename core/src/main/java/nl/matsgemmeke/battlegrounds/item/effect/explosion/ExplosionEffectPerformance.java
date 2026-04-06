package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.targeting.condition.HitboxTargetCondition;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import org.bukkit.Location;
import org.bukkit.World;

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
    public void start() {
        DamageSource damageSource = currentContext.getDamageSource();
        Actor actor = currentContext.getActor();
        Location actorLocation = actor.getLocation();
        World world = actor.getWorld();

        TargetQuery query = new TargetQuery()
                .uniqueId(damageSource.getUniqueId())
                .location(actorLocation)
                .conditions(new HitboxTargetCondition());

        for (DamageTarget damageTarget : targetFinder.findTargets(query)) {
            Location damageTargetLocation = damageTarget.getLocation();
            Damage damage = this.getDamageForTargetLocation(actorLocation, damageTargetLocation);

            DamageContext damageContext = new DamageContext(damageSource, damageTarget, damage);
            damageProcessor.processDamage(damageContext);
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
