package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class SoundEffect implements ProjectileEffect {

    private static final long RUNNABLE_PERIOD = 1L;

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final SoundProperties properties;
    @NotNull
    private final TaskRunner taskRunner;
    private BukkitTask task;
    private long elapsedTicks;

    @Inject
    public SoundEffect(@NotNull TaskRunner taskRunner, @Assisted @NotNull SoundProperties properties, @Assisted @NotNull AudioEmitter audioEmitter) {
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.properties = properties;
        this.elapsedTicks = 0;
    }

    public void onLaunch(@NotNull Projectile projectile) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(projectile), properties.delay(), RUNNABLE_PERIOD);
    }

    private void runCheck(@NotNull Projectile projectile) {
        if (!projectile.exists()) {
            task.cancel();
            return;
        }

        elapsedTicks++;

        if (!properties.intervals().contains(elapsedTicks)) {
            return;
        }

        audioEmitter.playSounds(properties.sounds(), projectile.getLocation());
    }
}
