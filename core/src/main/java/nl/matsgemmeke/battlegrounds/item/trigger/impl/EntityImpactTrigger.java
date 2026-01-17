package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.DamageTargetTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Trigger that activates when the target is about to hit an entity.
 */
public class EntityImpactTrigger implements Trigger {

    private static final double RAY_SIZE = 1.0;

    private final GameEntityFinder gameEntityFinder;

    @Inject
    public EntityImpactTrigger(GameEntityFinder gameEntityFinder) {
        this.gameEntityFinder = gameEntityFinder;
    }

    @Override
    public boolean activates(TriggerContext context) {
        return false;
    }

    @Override
    public TriggerResult check(TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        Vector velocity = target.getVelocity();
        Location projectileLocation = target.getLocation();

        // Stop the current check if the projectile does not move, because we cannot cast a ray trace with zero magnitude
        if (velocity.isZero()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        World world = target.getWorld();
        double rayDistance = velocity.length();
        HitEntityFilter entityFilter = new HitEntityFilter(context);
        RayTraceResult rayTraceResult = world.rayTraceEntities(projectileLocation, velocity, rayDistance, RAY_SIZE, entityFilter);

        if (rayTraceResult == null) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        Entity hitEntity = rayTraceResult.getHitEntity();

        if (hitEntity == null) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        UUID uniqueId = hitEntity.getUniqueId();
        GameEntity gameEntity = gameEntityFinder.findGameEntityByUniqueId(uniqueId).orElse(null);

        if (gameEntity == null) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        Vector hitPosition = rayTraceResult.getHitPosition();
        Location hitLocation = hitPosition.toLocation(world);

        return new DamageTargetTriggerResult(gameEntity, hitLocation);
    }
}
