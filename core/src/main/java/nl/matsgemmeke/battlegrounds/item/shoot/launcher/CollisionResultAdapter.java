package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitResult;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter.BlockTriggerResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter.DamageTargetTriggerResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter.SimpleTriggerResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter.TriggerResultAdapter;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.DamageTargetTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Util class that converts given objects into {@link CollisionResult} instances.
 */
public class CollisionResultAdapter {

    private static final double BLOCK_CENTER_OFFSET = 0.5;
    private static final double RAY_TRACE_LENGTH_MULTIPLIER = 2.0;
    private static final Map<Class<? extends TriggerResult>, TriggerResultAdapter<?>> TRIGGER_RESULT_ADAPTERS = new HashMap<>();

    static {
        TRIGGER_RESULT_ADAPTERS.put(SimpleTriggerResult.class, new SimpleTriggerResultAdapter());
        TRIGGER_RESULT_ADAPTERS.put(BlockTriggerResult.class, new BlockTriggerResultAdapter());
        TRIGGER_RESULT_ADAPTERS.put(DamageTargetTriggerResult.class, new DamageTargetTriggerResultAdapter());
    }

    private final GameEntityFinder gameEntityFinder;

    @Inject
    public CollisionResultAdapter(GameEntityFinder gameEntityFinder) {
        this.gameEntityFinder = gameEntityFinder;
    }

    public CollisionResult adapt(ProjectileHitResult projectileHitResult, Projectile projectile) {
        Block hitBlock = projectileHitResult.hitBlock();
        Entity hitEntity = projectileHitResult.hitEntity();

        Location projectileLocation = projectile.getLocation();
        Vector projectileVelocity = projectile.getVelocity();
        double length = projectileVelocity.length() * RAY_TRACE_LENGTH_MULTIPLIER;
        World world = projectile.getWorld();

        if (hitEntity != null) {
            DamageTarget hitTarget = gameEntityFinder.findGameEntityByUniqueId(hitEntity.getUniqueId()).orElse(null);
            RayTraceResult rayTraceResult = world.rayTraceEntities(projectileLocation, projectileVelocity, length);

            if (rayTraceResult == null || rayTraceResult.getHitEntity() != hitEntity) {
                // When the ray trace cannot find a hit location, take the center of the hit entity
                BoundingBox boundingBox = hitEntity.getBoundingBox();
                Location hitLocation = hitEntity.getLocation().add(0, boundingBox.getMaxY() / 2, 0);

                return new CollisionResult(null, hitTarget, hitLocation);
            }

            Location hitLocation = rayTraceResult.getHitPosition().toLocation(world);
            return new CollisionResult(hitBlock, hitTarget, hitLocation);
        } else {
            RayTraceResult rayTraceResult = world.rayTraceBlocks(projectileLocation, projectileVelocity, length, FluidCollisionMode.NEVER);

            if (rayTraceResult == null || rayTraceResult.getHitBlock() != hitBlock) {
                // When the ray trace cannot find a hit location, take the center of the hit entity
                Location hitLocation = hitBlock.getLocation().add(BLOCK_CENTER_OFFSET, BLOCK_CENTER_OFFSET, BLOCK_CENTER_OFFSET);

                return new CollisionResult(hitBlock, null, hitLocation);
            }

            Location hitLocation = rayTraceResult.getHitPosition().toLocation(world);
            return new CollisionResult(hitBlock, null, hitLocation);
        }
    }

    public CollisionResult adapt(TriggerResult triggerResult) {
        TriggerResultAdapter<?> adapter = TRIGGER_RESULT_ADAPTERS.get(triggerResult.getClass());

        if (adapter == null) {
            throw new IllegalStateException("No TriggerResultAdapter registered for " + triggerResult.getClass().getSimpleName());
        }

        return adaptTriggerResult(adapter, triggerResult);
    }

    @SuppressWarnings("unchecked")
    private <T extends TriggerResult> CollisionResult adaptTriggerResult(TriggerResultAdapter<T> adapter, TriggerResult triggerResult) {
        return adapter.adapt((T) triggerResult);
    }
}
