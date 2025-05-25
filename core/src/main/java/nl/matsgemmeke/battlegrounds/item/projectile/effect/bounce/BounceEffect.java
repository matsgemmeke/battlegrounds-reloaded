package nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce;

import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectPerformanceException;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BounceEffect implements ProjectileEffect {

    private static final double RAY_TRACE_MAX_DISTANCE = 1.0;

    @NotNull
    private final BounceProperties properties;
    @NotNull
    private final Set<Trigger> triggers;
    private int bounces;

    public BounceEffect(@NotNull BounceProperties properties, @NotNull Set<Trigger> triggers) {
        this.properties = properties;
        this.triggers = triggers;
    }

    public void onLaunch(@NotNull Projectile projectile) { }

    public void onLaunch(@NotNull Entity deployerEntity, @NotNull Projectile projectile) {
        bounces = 0;

        TriggerContext context = new TriggerContext(deployerEntity, projectile);

        for (Trigger trigger : triggers) {
            trigger.addObserver(() -> this.performEffect(projectile));
            trigger.activate(context);
        }
    }

    private void performEffect(@NotNull Projectile projectile) {
        Vector velocity = projectile.getVelocity();
        Location projectileLocation = projectile.getLocation();
        World world = projectile.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(projectileLocation, velocity, RAY_TRACE_MAX_DISTANCE);

        if (rayTraceResult == null) {
            throw new ProjectileEffectPerformanceException("Expected the projectile to hit a block, but the ray trace is null");
        }

        BlockFace hitBlockFace = rayTraceResult.getHitBlockFace();

        if (hitBlockFace == null) {
            throw new ProjectileEffectPerformanceException("Expected the projectile to hit a block, but the hit block face is null");
        }

        bounces++;

        if (bounces >= properties.amountOfBounces()) {
            triggers.forEach(Trigger::deactivate);
        }

        // Create a reflection vector and add friction for the x and z movement
        Vector reflection = this.reflectVector(hitBlockFace, velocity);
        reflection.setX(reflection.getX() / properties.horizontalFriction());
        reflection.setY(reflection.getY() / properties.verticalFriction());
        reflection.setZ(reflection.getZ() / properties.horizontalFriction());

        projectile.setVelocity(reflection);
    }

    @NotNull
    private Vector reflectVector(@NotNull BlockFace hitBlockFace, @NotNull Vector projectileVelocity) {
        Vector normal = new Vector(hitBlockFace.getModX(), hitBlockFace.getModY(), hitBlockFace.getModZ());

        double dotProduct = projectileVelocity.dot(normal);

        return projectileVelocity.subtract(normal.multiply(dotProduct * 2));
    }
}