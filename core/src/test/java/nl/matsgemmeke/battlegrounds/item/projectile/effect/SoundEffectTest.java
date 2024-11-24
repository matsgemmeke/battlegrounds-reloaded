package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SoundEffectTest {

    private static final double FACTOR = 0.9;
    private static final int INTERVAL = 10;
    private static final Iterable<GameSound> SOUNDS = Collections.emptySet();

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

        SoundEffect effect = new SoundEffect(audioEmitter, taskRunner, SOUNDS, INTERVAL, FACTOR);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(1L));

        runnableCaptor.getValue().run();

        verify(task).cancel();
        verifyNoInteractions(audioEmitter);
    }
}
