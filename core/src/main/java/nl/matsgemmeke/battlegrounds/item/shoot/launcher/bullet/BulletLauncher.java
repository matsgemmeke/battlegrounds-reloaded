package nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet;

import com.google.inject.assistedinject.Assisted;
import jakarta.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BulletLauncher implements ProjectileLauncher {

    private static final double DISTANCE_JUMP = 0.5;
    private static final double DISTANCE_START = 0.5;

    @NotNull
    private final BulletProperties bulletProperties;
    @NotNull
    private final CollisionDetector collisionDetector;
    @NotNull
    private final ParticleEffectSpawner particleEffectSpawner;

    @Inject
    public BulletLauncher(
            @NotNull ParticleEffectSpawner particleEffectSpawner,
            @Assisted @NotNull BulletProperties bulletProperties,
            @Assisted @NotNull CollisionDetector collisionDetector
    ) {
        this.particleEffectSpawner = particleEffectSpawner;
        this.bulletProperties = bulletProperties;
        this.collisionDetector = collisionDetector;
    }

    public void launch(@NotNull Location launchDirection) {
        double distance = DISTANCE_START;
        double projectileRange = 50.0;

        Location projectileLocation = launchDirection.clone();

        do {
            Vector vector = projectileLocation.getDirection().multiply(distance);
            projectileLocation.add(vector);

            // Check if the projectile's current location causes a collision
            if (collisionDetector.producesBlockCollisionAt(projectileLocation)) {
                Block block = projectileLocation.getBlock();
                block.getWorld().playEffect(projectileLocation, Effect.STEP_SOUND, block.getType());
                break;
            }

            particleEffectSpawner.spawnParticleEffect(bulletProperties.trajectoryParticleEffect(), projectileLocation.getBlock().getWorld(), projectileLocation);

            projectileLocation.subtract(vector);

            distance += DISTANCE_JUMP;
        } while (distance < projectileRange);
    }
}
