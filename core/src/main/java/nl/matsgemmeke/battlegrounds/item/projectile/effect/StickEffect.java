package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
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
    private Iterable<GameSound> stickSounds;
    private long checkDelay;
    private long checkPeriod;
    @NotNull
    private TaskRunner taskRunner;

    public StickEffect(
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner,
            @NotNull Iterable<GameSound> stickSounds,
            long checkDelay,
            long checkPeriod
    ) {
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.stickSounds = stickSounds;
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

        Vector direction = projectile.getVelocity().normalize();
        Location locationInFront = projectile.getLocation().add(direction);
        Block blockInFront = projectile.getWorld().getBlockAt(locationInFront);

        if (!blockInFront.getType().isSolid()) {
            return;
        }

        audioEmitter.playSounds(stickSounds, projectile.getLocation());

        projectile.setGravity(false);
        projectile.setVelocity(new Vector(0, 0, 0));

        task.cancel();
    }
}
