package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * Trigger that activates when the target is about to hit an entity.
 */
public class EntityImpactTrigger implements Trigger {

    private static final double RAY_SIZE = 1.0;

    @Override
    public boolean activates(TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            return false;
        }

        Vector velocity = target.getVelocity();
        Location projectileLocation = target.getLocation();

        // Stop the current check if the projectile does not move, because we cannot cast a ray trace with zero magnitude
        if (velocity.isZero()) {
            return false;
        }

        double rayDistance = velocity.length();
        World world = target.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceEntities(projectileLocation, velocity, rayDistance, RAY_SIZE, null);

        if (rayTraceResult == null) {
            return false;
        }

        Entity hitEntity = rayTraceResult.getHitEntity();

        return hitEntity != null && !hitEntity.getUniqueId().equals(context.sourceId());
    }
}
