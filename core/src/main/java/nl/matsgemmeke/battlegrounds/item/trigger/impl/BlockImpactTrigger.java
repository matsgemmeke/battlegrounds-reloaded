package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.item.trigger.CheckResult;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Optional;

/**
 * Trigger that activates when the target is about to hit a block.
 */
public class BlockImpactTrigger implements Trigger {

    @Override
    public boolean activates(TriggerContext context) {
        return false;
    }

    @Override
    public Optional<CheckResult> check(TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            return Optional.empty();
        }

        Vector velocity = target.getVelocity();
        Location projectileLocation = target.getLocation();

        // Stop the current check if the projectile does not move, because we cannot cast a ray trace with zero magnitude
        if (velocity.isZero()) {
            return Optional.empty();
        }

        World world = target.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(projectileLocation, velocity, velocity.lengthSquared(), FluidCollisionMode.NEVER, true);

        if (rayTraceResult == null) {
            return Optional.empty();
        }

        Block hitBlock = rayTraceResult.getHitBlock();

        if (hitBlock == null || !hitBlock.getType().isSolid()) {
            return Optional.empty();
        }

        Vector hitPosition = rayTraceResult.getHitPosition();
        Location hitLocation = hitPosition.toLocation(world);

        return Optional.of(new CheckResult(null, hitBlock, hitLocation));
    }
}
