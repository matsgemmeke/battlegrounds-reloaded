package nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BounceEffectTest {

    private static final double HORIZONTAL_FRICTION = 1.0;
    private static final double VERTICAL_FRICTION = 1.0;
    private static final int AMOUNT_OF_BOUNCES = 1;
    private static final long CHECK_DELAY = 0L;
    private static final long CHECK_PERIOD = 1L;

    private BounceProperties properties;
    private Set<Trigger> triggers;
    private TaskRunner taskRunner;
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        properties = new BounceProperties(AMOUNT_OF_BOUNCES, HORIZONTAL_FRICTION, VERTICAL_FRICTION, CHECK_DELAY, CHECK_PERIOD);
        taskRunner = mock(TaskRunner.class);
        trigger = mock(Trigger.class);
        triggers = Set.of(trigger);
    }

    @Test
    public void onLaunchStopsCheckingOnceProjectileNoLongerExists() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(false);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        BounceEffect effect = new BounceEffect(taskRunner, properties, triggers);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void onLaunchDoesNotAlterProjectileIfCastRayTraceIsNull() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(null);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        BounceEffect effect = new BounceEffect(taskRunner, properties, triggers);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(projectile, never()).setVelocity(any(Vector.class));
        verify(task, never()).cancel();
    }

    @Test
    public void onLaunchDoesNotAlterProjectileIfCastRayTraceHasNoHitBlock() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), (Block) null, null);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        BounceEffect effect = new BounceEffect(taskRunner, properties, triggers);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(projectile, never()).setVelocity(any(Vector.class));
        verify(task, never()).cancel();
    }

    @Test
    public void onLaunchDoesNotAlterProjectileIfCastRayTraceHasNoHitBlockFace() {
        Block block = mock(Block.class);
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), block, null);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        BounceEffect effect = new BounceEffect(taskRunner, properties, triggers);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(projectile, never()).setVelocity(any(Vector.class));
        verify(task, never()).cancel();
    }

    @Test
    public void onLaunchDoesNotAlterProjectileIfHitBlockIsNotSolid() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.AIR);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), block, BlockFace.NORTH);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        BounceEffect effect = new BounceEffect(taskRunner, properties, triggers);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(projectile, never()).setVelocity(any(Vector.class));
        verify(task, never()).cancel();
    }

    @DisplayName("Projectile reflection scenarios")
    @NotNull
    private static Stream<Arguments> blockProjectileScenarios() {
        return Stream.of(
                arguments(BlockFace.UP, new Vector(0, -1, 0), new Vector(0, 1, 0)),
                arguments(BlockFace.DOWN, new Vector(0, 1, 0), new Vector(0, -1, 0)),
                arguments(BlockFace.WEST, new Vector(1, 0, 0), new Vector(-1, 0, 0)),
                arguments(BlockFace.EAST, new Vector(-1, 0, 0), new Vector(1, 0, 0)),
                arguments(BlockFace.NORTH, new Vector(0, 0, -1), new Vector(0, 0, 1)),
                arguments(BlockFace.SOUTH, new Vector(0, 0, 1), new Vector(0, 0, -1))
        );
    }

    @ParameterizedTest
    @MethodSource("blockProjectileScenarios")
    public void onLaunchAltersProjectileVelocity(
            BlockFace hitBlockFace,
            Vector velocity,
            Vector reflection
    ) {
        Location projectileLocation = new Location(null, 0, 0, 0);

        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.STONE);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), block, hitBlockFace);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        BounceEffect effect = new BounceEffect(taskRunner, properties, triggers);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(projectile).setVelocity(reflection);
        verify(task).cancel();
    }
}
