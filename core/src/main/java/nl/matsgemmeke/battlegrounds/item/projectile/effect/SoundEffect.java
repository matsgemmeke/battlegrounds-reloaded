package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SoundEffect implements ProjectileEffect {

    private static final long TIMER_DELAY = 0L;
    private static final long TIMER_PERIOD = 1L;

    @NotNull
    private AudioEmitter audioEmitter;
    private BukkitTask task;
    private int ticks;
    @NotNull
    private Iterable<GameSound> sounds;
    @NotNull
    private List<Integer> intervals;
    @NotNull
    private TaskRunner taskRunner;

    public SoundEffect(
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner,
            @NotNull Iterable<GameSound> sounds,
            @NotNull List<Integer> intervals
    ) {
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.sounds = sounds;
        this.intervals = intervals;
        this.ticks = 0;
    }

    public void onLaunch(@NotNull Projectile projectile) {
        task = taskRunner.runTaskTimer(() -> this.runCheck(projectile), TIMER_DELAY, TIMER_PERIOD);
    }

    private void runCheck(@NotNull Projectile projectile) {
        if (!projectile.exists()) {
            task.cancel();
            return;
        }

        ticks++;

        if (!intervals.contains(ticks)) {
            return;
        }

        audioEmitter.playSounds(sounds, projectile.getLocation());
    }
}
