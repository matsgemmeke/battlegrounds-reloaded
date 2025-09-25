package nl.matsgemmeke.battlegrounds.item.trigger.impact;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerNew;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class ImpactTrigger implements TriggerNew {

    private static final double RAY_TRACE_MAX_DISTANCE = 1.0;

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

        World world = target.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(projectileLocation, velocity, RAY_TRACE_MAX_DISTANCE);

        return rayTraceResult != null
                && rayTraceResult.getHitBlock() != null
                && rayTraceResult.getHitBlockFace() != null
                && rayTraceResult.getHitBlock().getType().isSolid();
    }
}
