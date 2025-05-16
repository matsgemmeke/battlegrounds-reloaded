package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class StickEffect implements ProjectileEffect {

    @NotNull
    private AudioEmitter audioEmitter;
    private BukkitTask task;
    @NotNull
    private StickProperties properties;
    @NotNull
    private TaskRunner taskRunner;

    @Inject
    public StickEffect(@NotNull TaskRunner taskRunner, @Assisted @NotNull StickProperties properties, @Assisted @NotNull AudioEmitter audioEmitter) {
        this.taskRunner = taskRunner;
        this.properties = properties;
        this.audioEmitter = audioEmitter;
    }

    public void onLaunch(@NotNull Projectile projectile) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(projectile), 1L, 1L);
    }

    private void runCheck(@NotNull Projectile projectile) {
        if (!projectile.exists()) {
            task.cancel();
            return;
        }

        Vector direction = projectile.getVelocity().normalize();
        Location locationInFront = projectile.getLocation().add(direction);
        Block blockInFront = projectile.getWorld().getBlockAt(locationInFront);

        if (!blockInFront.getType().isSolid()) {
            return;
        }

        audioEmitter.playSounds(properties.stickSounds(), projectile.getLocation());

        projectile.setGravity(false);
        projectile.setVelocity(new Vector(0, 0, 0));

        task.cancel();
    }
}
