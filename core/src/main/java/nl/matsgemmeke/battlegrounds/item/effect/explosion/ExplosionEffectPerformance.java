package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.damage.Damage;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.effect.ExplosionAttributor;
import nl.matsgemmeke.battlegrounds.game.component.effect.ExplosionAttributorRegistry;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.targeting.condition.ProximityTargetCondition;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

public class ExplosionEffectPerformance extends BaseItemEffectPerformance {

    private final DamageProcessor damageProcessor;
    private final ExplosionAttributorRegistry explosionAttributorRegistry;
    private final ExplosionProperties properties;
    private final TargetFinder targetFinder;

    @Inject
    public ExplosionEffectPerformance(
            DamageProcessor damageProcessor,
            ExplosionAttributorRegistry explosionAttributorRegistry,
            TargetFinder targetFinder,
            @Assisted ExplosionProperties properties
    ) {
        this.damageProcessor = damageProcessor;
        this.explosionAttributorRegistry = explosionAttributorRegistry;
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
        World actorWorld = actor.getWorld();

        double maxDistance = properties.rangeProfile().longRangeDistance();

        TargetQuery query = new TargetQuery()
                .uniqueId(damageSource.getUniqueId())
                .location(actorLocation)
                .conditions(new ProximityTargetCondition(maxDistance));

        for (DamageTarget damageTarget : targetFinder.findTargets(query)) {
            Location damageTargetLocation = damageTarget.getLocation();
            double distance = actorLocation.distance(damageTargetLocation);

            String itemName = currentContext.getItemName();
            double damageAmount = properties.rangeProfile().getDamageByDistance(distance);
            Damage damage = new Damage(damageAmount, DamageType.EXPLOSIVE_DAMAGE, HitboxComponentType.TORSO);

            DamageContext damageContext = new DamageContext(damageSource, damageTarget, itemName, damage, distance);
            damageProcessor.processDamage(damageContext);
        }

        // Remove the source before creating the explosion to prevent calling an extra EntityDamageByEntityEvent
        if (actor instanceof Removable removableActor) {
            removableActor.remove();
        }

        ArmorStand armorStand = actorWorld.spawn(actorLocation, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        ExplosionAttributor attributor = new ExplosionAttributor(armorStand.getUniqueId());
        explosionAttributorRegistry.addAttributor(attributor);

        actorWorld.createExplosion(actorLocation, properties.power(), properties.setFire(), properties.breakBlocks(), armorStand);

        explosionAttributorRegistry.removeAttributor(attributor);
        armorStand.remove();
    }
}
