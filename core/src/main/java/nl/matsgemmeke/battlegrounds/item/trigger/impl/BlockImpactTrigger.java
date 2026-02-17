package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * Trigger that activates when the actor is about to hit a block.
 */
public class BlockImpactTrigger implements Trigger {

    @Override
    public TriggerResult check(TriggerContext context) {
        Actor actor = context.actor();

        if (!actor.exists()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        Vector velocity = actor.getVelocity();
        Location actorLocation = actor.getLocation();

        // Stop the current check if the actor does not move, because we cannot cast a ray trace with zero magnitude
        if (velocity.isZero()) {
            return SimpleTriggerResult.NOT_ACTIVATES;
        }

        World world = actor.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(actorLocation, velocity, velocity.lengthSquared(), FluidCollisionMode.NEVER, true);

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
