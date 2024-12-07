package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BounceEffect implements ProjectileEffect {

    private static final double RAY_TRACE_MAX_DISTANCE = 1.0;

    @NotNull
    private BounceProperties properties;
    private BukkitTask task;
    private int bounces;
    @NotNull
    private TaskRunner taskRunner;

    public BounceEffect(@NotNull TaskRunner taskRunner, @NotNull BounceProperties properties) {
        this.taskRunner = taskRunner;
        this.properties = properties;
    }

    public void onLaunch(@NotNull Projectile projectile) {
        bounces = 0;
        task = taskRunner.runTaskTimer(() -> this.runCheck(projectile), properties.checkDelay(), properties.checkPeriod());
    }

    private void runCheck(@NotNull Projectile projectile) {
        if (!projectile.exists()) {
            task.cancel();
            return;
        }

        Vector velocity = projectile.getVelocity();
        Location projectileLocation = projectile.getLocation();

        World world = projectile.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(projectileLocation, velocity, RAY_TRACE_MAX_DISTANCE);

        if (rayTraceResult == null
                || rayTraceResult.getHitBlock() == null
                || rayTraceResult.getHitBlockFace() == null
                || !rayTraceResult.getHitBlock().getType().isSolid()) {
            return;
        }

        bounces++;

        if (bounces >= properties.amountOfBounces()) {
            task.cancel();
        }

        // Create a reflection vector and add friction for the x and z movement
        Vector reflection = this.reflectVector(rayTraceResult.getHitBlockFace(), velocity);
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