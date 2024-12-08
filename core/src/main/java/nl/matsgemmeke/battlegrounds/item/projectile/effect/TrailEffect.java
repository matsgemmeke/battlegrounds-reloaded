package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class TrailEffect implements ProjectileEffect {

    private BukkitTask task;
    @NotNull
    private TaskRunner taskRunner;
    @NotNull
    private TrailProperties properties;

    public TrailEffect(@NotNull TaskRunner taskRunner, @NotNull TrailProperties properties) {
        this.taskRunner = taskRunner;
        this.properties = properties;
    }

    @Override
    public void onLaunch(@NotNull Projectile projectile) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(projectile), properties.checkDelay(), properties.checkPeriod());
    }

    private void runCheck(@NotNull Projectile projectile) {
        if (!projectile.exists()) {
            task.cancel();
            return;
        }

        Particle particle = properties.particleEffect().type();
        Location location = projectile.getLocation();
        int count = properties.particleEffect().count();
        double offsetX = properties.particleEffect().offsetX();
        double offsetY = properties.particleEffect().offsetY();
        double offsetZ = properties.particleEffect().offsetZ();
        double extra = properties.particleEffect().extra();

        World world = projectile.getWorld();
        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
    }
}
