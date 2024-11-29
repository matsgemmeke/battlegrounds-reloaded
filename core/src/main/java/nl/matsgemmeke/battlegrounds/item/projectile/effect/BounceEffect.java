package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BounceEffect implements ProjectileEffect {

    private static final double MIN_Y_MOVEMENT = 0.01;
    private static final double Y_SUBTRACTION = 0.1;

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

        Location locationBelow = projectile.getLocation().subtract(0, Y_SUBTRACTION, 0);
        Block blockBelow = locationBelow.getBlock();
        Vector velocity = projectile.getVelocity();

        if (!blockBelow.getType().isSolid() || velocity.getY() < MIN_Y_MOVEMENT) {
            return;
        }

        bounces++;

        velocity.setY(-velocity.getY() * properties.bounceFactor());
        velocity.setX(velocity.getX() * properties.velocityRetention());
        velocity.setZ(velocity.getZ() * properties.velocityRetention());

        projectile.setVelocity(velocity);

        if (bounces < properties.amountOfBounces()) {
            return;
        }

        task.cancel();
    }
}
