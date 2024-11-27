package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BounceEffect implements ProjectileEffect {

    private BukkitTask task;
    private int bounces;
    private int amountOfBounces;
    private long checkDelay;
    private long checkPeriod;
    @NotNull
    private TaskRunner taskRunner;

    public BounceEffect(@NotNull TaskRunner taskRunner, int amountOfBounces, long checkDelay, long checkPeriod) {
        this.taskRunner = taskRunner;
        this.amountOfBounces = amountOfBounces;
        this.checkDelay = checkDelay;
        this.checkPeriod = checkPeriod;
    }

    public void onLaunch(@NotNull Projectile projectile) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(projectile), checkDelay, checkPeriod);
    }

    private void runCheck(@NotNull Projectile projectile) {
        if (!projectile.exists()) {
            task.cancel();
            return;
        }

        Vector velocity = projectile.getVelocity();
        Vector direction = velocity.normalize();
        Location locationInFront = projectile.getLocation().add(direction);
        Block blockInFront = projectile.getWorld().getBlockAt(locationInFront);

        if (!blockInFront.getType().isSolid()) {
            return;
        }

        bounces++;

        velocity.setY(velocity.getY() * -1);
        projectile.setVelocity(velocity);

        if (bounces < amountOfBounces) {
            return;
        }

        task.cancel();
    }
}
