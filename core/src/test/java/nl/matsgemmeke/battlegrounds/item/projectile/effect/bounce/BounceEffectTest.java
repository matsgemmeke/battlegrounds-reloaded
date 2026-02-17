package nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectPerformanceException;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BounceEffectTest {

    private static final int AMOUNT_OF_BOUNCES = 1;
    private static final double HORIZONTAL_FRICTION = 1.0;
    private static final double VERTICAL_FRICTION = 1.0;
    private static final BounceProperties PROPERTIES = new BounceProperties(AMOUNT_OF_BOUNCES, HORIZONTAL_FRICTION, VERTICAL_FRICTION);

    @Mock
    private DamageSource damageSource;
    @Mock
    private Projectile projectile;
    @Mock
    private TriggerExecutor triggerExecutor;
    @Mock
    private TriggerResult triggerResult;
    @Mock
    private TriggerRun triggerRun;

    private BounceEffect effect;

    @BeforeEach
    void setUp() {
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        effect = new BounceEffect(PROPERTIES);
    }

    @Test
    void onLaunchThrowsProjectileEffectPerformanceExceptionWhenRayTraceResultIsNull() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(null);

        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(damageSource, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        assertThatThrownBy(() -> triggerObserverCaptor.getValue().onActivate(triggerResult))
                .isInstanceOf(ProjectileEffectPerformanceException.class)
                .hasMessage("Expected the projectile to hit a block, but the ray trace is null");

        verify(projectile, never()).setVelocity(any(Vector.class));
        verify(triggerRun).start();
    }

    @Test
    void onLaunchThrowsProjectileEffectPerformanceExceptionWhenRayTraceResultHasNoHitBlockFace() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), (Block) null, null);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(damageSource, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        assertThatThrownBy(() -> triggerObserverCaptor.getValue().onActivate(triggerResult))
                .isInstanceOf(ProjectileEffectPerformanceException.class)
                .hasMessage("Expected the projectile to hit a block, but the hit block face is null");

        verify(projectile, never()).setVelocity(any(Vector.class));
        verify(triggerRun).start();
    }

    @Test
    void onLaunchStartsTriggerRunThatCancelWhenProjectileNoLongerExists() {
        when(projectile.exists()).thenReturn(false);

        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(damageSource, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate(triggerResult);

        verify(triggerRun).cancel();
        verify(projectile, never()).setGravity(anyBoolean());
        verify(projectile, never()).setVelocity(any(Vector.class));
    }

    @DisplayName("Projectile reflection scenarios")
    static Stream<Arguments> blockProjectileScenarios() {
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
    void onLaunchStartsTriggerWithObserverThatAltersProjectileVelocity(BlockFace hitBlockFace, Vector velocity, Vector reflection) {
        Location projectileLocation = new Location(null, 0, 0, 0);
        Block block = mock(Block.class);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), block, hitBlockFace);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(velocity);
        when(projectile.getWorld()).thenReturn(world);

        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(damageSource, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate(triggerResult);

        verify(projectile).setVelocity(reflection);
        verify(triggerRun).cancel();
    }
}
