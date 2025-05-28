package nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce;

import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectPerformanceException;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BounceEffectTest {

    private static final double HORIZONTAL_FRICTION = 1.0;
    private static final double VERTICAL_FRICTION = 1.0;
    private static final int AMOUNT_OF_BOUNCES = 1;

    private BounceProperties properties;
    private Entity deployerEntity;
    private Projectile projectile;
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        properties = new BounceProperties(AMOUNT_OF_BOUNCES, HORIZONTAL_FRICTION, VERTICAL_FRICTION);
        deployerEntity = mock(Entity.class);
        projectile = mock(Projectile.class);
        trigger = mock(Trigger.class);
    }

    @Test
    public void onLaunchThrowsProjectileEffectPerformanceExceptionWhenRayTraceResultIsNull() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(null);

        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BounceEffect effect = new BounceEffect(properties);
        effect.addTrigger(trigger);
        effect.onLaunch(deployerEntity, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        assertThatThrownBy(() -> triggerObserverCaptor.getValue().onActivate())
                .isInstanceOf(ProjectileEffectPerformanceException.class)
                .hasMessage("Expected the projectile to hit a block, but the ray trace is null");

        verify(projectile, never()).setVelocity(any(Vector.class));
        verify(trigger).start(any(TriggerContext.class));
    }

    @Test
    public void onLaunchThrowsProjectileEffectPerformanceExceptionWhenRayTraceResultHasNoHitBlockFace() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), (Block) null, null);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BounceEffect effect = new BounceEffect(properties);
        effect.addTrigger(trigger);
        effect.onLaunch(deployerEntity, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        assertThatThrownBy(() -> triggerObserverCaptor.getValue().onActivate())
                .isInstanceOf(ProjectileEffectPerformanceException.class)
                .hasMessage("Expected the projectile to hit a block, but the hit block face is null");

        verify(projectile, never()).setVelocity(any(Vector.class));
        verify(trigger).start(any(TriggerContext.class));
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
    public void onLaunchStartsTriggerWithObserverThatAltersProjectileVelocity(BlockFace hitBlockFace, Vector velocity, Vector reflection) {
        Location projectileLocation = new Location(null, 0, 0, 0);

        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.STONE);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), block, hitBlockFace);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        BounceEffect effect = new BounceEffect(properties);
        effect.addTrigger(trigger);
        effect.onLaunch(deployerEntity, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(projectile).setVelocity(reflection);
        verify(trigger).stop();
    }
}
