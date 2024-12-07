package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SoundEffectTest {

    private static final Iterable<GameSound> SOUNDS = Collections.emptySet();
    private static final List<Integer> INTERVALS = List.of(10, 19, 27, 34, 40, 45, 49, 52, 54, 56, 58);

    private AudioEmitter audioEmitter;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void onLaunchStopsCheckOnceProjectileNoLongerExists() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(false);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(1L))).thenReturn(task);

        SoundEffect effect = new SoundEffect(audioEmitter, taskRunner, SOUNDS, INTERVALS);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(1L));

        runnableCaptor.getValue().run();

        verify(task).cancel();
        verifyNoInteractions(audioEmitter);
    }

    @Test
    public void onLaunchPlaysSoundBasedOnIntervals() {
        Location projectileLocation = new Location(null, 1, 1, 1);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(1L))).thenReturn(task);

        SoundEffect effect = new SoundEffect(audioEmitter, taskRunner, SOUNDS, INTERVALS);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(1L));

        for (int i = 0; i < 60; i++) {
            runnableCaptor.getValue().run();
        }

        verify(audioEmitter, times(11)).playSounds(SOUNDS, projectileLocation);
    }
}
