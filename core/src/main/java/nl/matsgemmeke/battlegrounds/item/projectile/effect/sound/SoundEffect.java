package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class SoundEffect implements ProjectileEffect {

    private static final long CHECK_DELAY = 0L;
    private static final long CHECK_PERIOD = 1L;

    @NotNull
    private AudioEmitter audioEmitter;
    private BukkitTask task;
    private int ticks;
    @NotNull
    private SoundProperties properties;
    @NotNull
    private TaskRunner taskRunner;

    public SoundEffect(@NotNull AudioEmitter audioEmitter, @NotNull TaskRunner taskRunner, @NotNull SoundProperties properties) {
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.properties = properties;
        this.ticks = 0;
    }

    public void onLaunch(@NotNull Projectile projectile) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(projectile), CHECK_DELAY, CHECK_PERIOD);
    }

    private void runCheck(@NotNull Projectile projectile) {
        if (!projectile.exists()) {
            task.cancel();
            return;
        }

        ticks++;

        if (!properties.intervals().contains(ticks)) {
            return;
        }

        audioEmitter.playSounds(properties.sounds(), projectile.getLocation());
    }
}
