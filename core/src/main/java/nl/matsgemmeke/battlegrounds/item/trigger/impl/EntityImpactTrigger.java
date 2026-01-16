package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.CheckResult;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Optional;
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
        double rayDistance = velocity.length();
        HitEntityFilter entityFilter = new HitEntityFilter(context);
        RayTraceResult rayTraceResult = world.rayTraceEntities(projectileLocation, velocity, rayDistance, RAY_SIZE, entityFilter);

        if (rayTraceResult == null) {
            return Optional.empty();
        }

        Entity hitEntity = rayTraceResult.getHitEntity();

        if (hitEntity == null) {
            return Optional.empty();
        }

        UUID uniqueId = hitEntity.getUniqueId();
        GameEntity gameEntity = gameEntityFinder.findGameEntityByUniqueId(uniqueId).orElse(null);

        if (gameEntity == null) {
            return Optional.empty();
        }

        Vector hitPosition = rayTraceResult.getHitPosition();
        Location hitLocation = hitPosition.toLocation(world);

        return Optional.of(new CheckResult(gameEntity, null, hitLocation));
    }
}
