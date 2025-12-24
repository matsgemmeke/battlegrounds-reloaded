package nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static nl.matsgemmeke.battlegrounds.MockUtils.RUN_PROJECTILE_HIT_ACTION;
import static nl.matsgemmeke.battlegrounds.MockUtils.RUN_SCHEDULE_TASK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArrowLauncherTest {

    private static final double VELOCITY = 3.0;
    private static final long GAME_SOUND_DELAY = 5L;
    private static final Location LAUNCH_DIRECTION = new Location(null, 1, 1, 1);

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private DamageSource damageSource;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ProjectileHitActionRegistry projectileHitActionRegistry;
    @Mock
    private ProjectileLaunchSource projectileSource;
    @Mock
    private Scheduler scheduler;

    @Test
    void cancelStopsOngoingSoundPlaySchedules() {
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Arrow arrow = mock(Arrow.class);
        Schedule soundPlaySchedule = mock(Schedule.class);

        when(projectileSource.launchProjectile(eq(Arrow.class), any(Vector.class))).thenReturn(arrow);

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        ArrowProperties properties = new ArrowProperties(List.of(gameSound), VELOCITY);
        LaunchContext launchContext = new LaunchContext(damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);

        ArrowLauncher launcher = new ArrowLauncher(audioEmitter, projectileHitActionRegistry, scheduler, properties, itemEffect);
        launcher.launch(launchContext);
        launcher.cancel();

        verify(soundPlaySchedule).stop();
    }

    @Test
    void launchLaunchesArrowProjectile() {
        World world = mock(World.class);
        Location arrowLocation = new Location(world, 10.0, 10.0, 10.0);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        Arrow arrow = mock(Arrow.class);
        when(arrow.getLocation()).thenReturn(arrowLocation);
        when(arrow.getWorld()).thenReturn(world);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        ProjectileLaunchSource source = mock(ProjectileLaunchSource.class);
        when(source.launchProjectile(eq(Arrow.class), any(Vector.class))).thenReturn(arrow);

        ArrowProperties properties = new ArrowProperties(List.of(gameSound), VELOCITY);
        LaunchContext launchContext = new LaunchContext(damageSource, source, direction, () -> LAUNCH_DIRECTION, world);

        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);
        doAnswer(RUN_PROJECTILE_HIT_ACTION).when(projectileHitActionRegistry).registerProjectileHitAction(eq(arrow), any(ProjectileHitAction.class));

        ArrowLauncher launcher = new ArrowLauncher(audioEmitter, projectileHitActionRegistry, scheduler, properties, itemEffect);
        launcher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(damageSource);
            assertThat(itemEffectContext.getEffectSource().getLocation()).isEqualTo(arrowLocation);
            assertThat(itemEffectContext.getEffectSource().getWorld()).isEqualTo(world);
            assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(direction);
        });

        verify(arrow).setVelocity(new Vector(0, 0, 3.0));
        verify(audioEmitter).playSound(gameSound, LAUNCH_DIRECTION);
        verify(projectileHitActionRegistry).removeProjectileHitAction(arrow);
    }
}
