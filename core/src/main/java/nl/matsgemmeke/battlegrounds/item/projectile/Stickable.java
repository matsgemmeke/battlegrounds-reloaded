package nl.matsgemmeke.battlegrounds.item.projectile;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Stickable implements ProjectileProperty {

    private long checkDelay;
    private long checkPeriod;
    @NotNull
    private TaskRunner taskRunner;

    public Stickable(@NotNull TaskRunner taskRunner, long checkDelay, long checkPeriod) {
        this.taskRunner = taskRunner;
        this.checkDelay = checkDelay;
        this.checkPeriod = checkPeriod;
    }

    public void onLaunch(@NotNull Projectile projectile) {
        taskRunner.runTaskTimer(() -> this.runCheck(projectile), checkDelay, checkPeriod);
    }

    private void runCheck(@NotNull Projectile projectile) {
        Vector direction = projectile.getVelocity().normalize();
        Location locationInFront = projectile.getLocation().add(direction);
        Block blockInFront = projectile.getWorld().getBlockAt(locationInFront);

        if (!blockInFront.getType().isSolid()) {
            return;
        }

        projectile.setGravity(false);
        projectile.setVelocity(new Vector(0, 0, 0));
    }
}
