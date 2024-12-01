package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BounceEffect implements ProjectileEffect {

    private static final double CENTER_OFFSET = 0.5;

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
        Location locationInFront = projectileLocation.clone().add(velocity);
        Block blockInFront = projectile.getWorld().getBlockAt(locationInFront);

        if (!blockInFront.getType().isSolid()) {
            return;
        }

        bounces++;

        if (bounces >= properties.amountOfBounces()) {
            task.cancel();
        }

        // Create a reflection vector and add friction for the x and z movement
        Vector reflection = this.reflectVector(blockInFront, projectileLocation, velocity);
        reflection.setX(reflection.getX() * properties.frictionFactor());
        reflection.setZ(reflection.getZ() * properties.frictionFactor());

        projectile.setVelocity(reflection);
    }

    @NotNull
    private Vector reflectVector(@NotNull Block block, @NotNull Location projectileLocation, @NotNull Vector projectileVelocity) {
        Vector normal = new Vector(0, 0, 0);

        double blockX = block.getX() + CENTER_OFFSET;
        double blockY = block.getY() + CENTER_OFFSET;
        double blockZ = block.getZ() + CENTER_OFFSET;

        double projectileX = projectileLocation.getX();
        double projectileY = projectileLocation.getY();
        double projectileZ = projectileLocation.getZ();

        if (projectileLocation.getBlockY() != block.getY()) {
            normal.setY(projectileY < blockY ? -1 : 1);
        } else {
            if (Math.abs(projectileX - blockX) > Math.abs(projectileZ - blockZ)) {
                // X-side collision (left/right wall)
                normal.setX(projectileX < blockX ? -1 : 1);
            } else {
                // Z-side collision (front/back wall)
                normal.setZ(projectileZ < blockZ ? -1 : 1);
            }
        }

        double dotProduct = projectileVelocity.dot(normal);

        return projectileVelocity.subtract(normal.multiply(dotProduct * 2));
    }
}
