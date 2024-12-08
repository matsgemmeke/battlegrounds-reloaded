package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class StickEffectTest {

    private static final List<GameSound> STICK_SOUNDS = Collections.emptyList();
    private static final long CHECK_DELAY = 0L;
    private static final long CHECK_PERIOD = 1L;

    private AudioEmitter audioEmitter;
    private StickProperties properties;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        properties = new StickProperties(STICK_SOUNDS, CHECK_DELAY, CHECK_PERIOD);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void onLaunchStopsCheckingIfProjectileDoesNotExist() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(false);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        StickEffect effect = new StickEffect(audioEmitter, taskRunner, properties);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void onLaunchDoesNotAlterProjectileIfBlockInFrontIsNotSolid() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 1, 1);

        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.AIR);

        World world = mock(World.class);
        when(world.getBlockAt(any(Location.class))).thenReturn(block);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        StickEffect effect = new StickEffect(audioEmitter, taskRunner, properties);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(projectile, never()).setGravity(anyBoolean());
        verify(projectile, never()).setVelocity(any(Vector.class));
        verify(task, never()).cancel();
    }

    @Test
    public void onLaunchSetsProjectileVelocityToZeroIfBlockInFrontIsSolid() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 1, 1);

        Block solidBlock = mock(Block.class);
        when(solidBlock.getType()).thenReturn(Material.STONE);

        World world = mock(World.class);
        when(world.getBlockAt(any(Location.class))).thenReturn(solidBlock);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        StickEffect effect = new StickEffect(audioEmitter, taskRunner, properties);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(STICK_SOUNDS, projectileLocation);
        verify(projectile).setGravity(false);
        verify(projectile).setVelocity(new Vector(0, 0, 0));
        verify(task).cancel();
    }
}
