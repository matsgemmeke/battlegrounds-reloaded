package nl.matsgemmeke.battlegrounds.item.projectile.effect.trail;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class TrailEffect implements ProjectileEffect {

    private static final long RUNNABLE_PERIOD = 1L;

    @NotNull
    private final ParticleEffectSpawner particleEffectSpawner;
    @NotNull
    private final TaskRunner taskRunner;
    @NotNull
    private final TrailProperties properties;
    private BukkitTask task;

    @Inject
    public TrailEffect(@NotNull ParticleEffectSpawner particleEffectSpawner, @NotNull TaskRunner taskRunner, @Assisted @NotNull TrailProperties properties) {
        this.particleEffectSpawner = particleEffectSpawner;
        this.taskRunner = taskRunner;
        this.properties = properties;
    }

    @Override
    public void onLaunch(@NotNull Projectile projectile) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(projectile), properties.delay(), RUNNABLE_PERIOD);
    }

    public void onLaunch(@NotNull Entity deployerEntity, @NotNull Projectile projectile) {
        throw new UnsupportedOperationException();
    }

    private void runCheck(@NotNull Projectile projectile) {
        if (!projectile.exists()) {
            task.cancel();
            return;
        }

        Location location = projectile.getLocation();
        World world = projectile.getWorld();

        particleEffectSpawner.spawnParticleEffect(properties.particleEffect(), world, location);
    }
}
