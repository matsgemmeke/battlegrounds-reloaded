package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BounceEffectTest {

    private static final double FRICTION_FACTOR = 0.5;
    private static final int AMOUNT_OF_BOUNCES = 1;
    private static final long CHECK_DELAY = 0L;
    private static final long CHECK_PERIOD = 1L;

    private BounceProperties properties;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        properties = new BounceProperties(AMOUNT_OF_BOUNCES, FRICTION_FACTOR, CHECK_DELAY, CHECK_PERIOD);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void onLaunchStopsCheckingOnceProjectileNoLongerExists() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(false);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        BounceEffect effect = new BounceEffect(taskRunner, properties);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void onLaunchDoesNotAlterProjectileIfBlockInFrontIsNotSolid() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

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

        BounceEffect effect = new BounceEffect(taskRunner, properties);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(projectile, never()).setGravity(anyBoolean());
        verify(projectile, never()).setVelocity(any(Vector.class));
        verify(task, never()).cancel();
    }

    static Stream<Arguments> blockProjectileScenarios() {
        return Stream.of(
                arguments(
                        new Location(null, 0, 0, 0),
                        new Location(null, 0, 1, 0),
                        new Vector(0.5, -1, 0.5),
                        new Vector(0.25, 1, 0.25)
                ),
                arguments(
                        new Location(null, 0, 0, 0),
                        new Location(null, -1, 0, 0),
                        new Vector(1, 0, 0),
                        new Vector(-0.5, 0, 0)
                ),
                arguments(
                        new Location(null, 0, 0, 0),
                        new Location(null, 0, 0, -1),
                        new Vector(0, 0, 1),
                        new Vector(0, 0, -0.5)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("blockProjectileScenarios")
    public void onLaunchAltersProjectileVelocity(Location blockLocation, Location projectileLocation, Vector velocity, Vector reflection) {
        Block block = mock(Block.class);
        when(block.getLocation()).thenReturn(blockLocation);
        when(block.getType()).thenReturn(Material.STONE);

        World world = mock(World.class);
        when(world.getBlockAt(any(Location.class))).thenReturn(block);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        BounceEffect effect = new BounceEffect(taskRunner, properties);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(projectile).setVelocity(reflection);
        verify(task).cancel();
    }
}
