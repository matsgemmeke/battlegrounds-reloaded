package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * Trigger that activates when the target is about to hit a block.
 */
public class BlockImpactTrigger implements Trigger {

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
        RayTraceResult rayTraceResult = world.rayTraceBlocks(projectileLocation, velocity, velocity.lengthSquared(), FluidCollisionMode.NEVER, true);

        if (rayTraceResult == null) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        Block hitBlock = rayTraceResult.getHitBlock();

        if (hitBlock == null || !hitBlock.getType().isSolid()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        Vector hitPosition = rayTraceResult.getHitPosition();
        Location hitLocation = hitPosition.toLocation(world);

        return new BlockTriggerResult(hitBlock, hitLocation);
    }
}
