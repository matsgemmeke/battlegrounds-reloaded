package nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.actor.ProjectileActor;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultMapper;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

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
    private static final UUID ARROW_UNIQUE_ID = UUID.randomUUID();

    @Mock
    private AudioEmitter audioEmitter;
    @Spy
    private CollisionResultMapper collisionResultMapper = new CollisionResultMapper();
    @Mock
    private DamageSource damageSource;
    @Mock
    private GameEntityFinder gameEntityFinder;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ProjectileHitActionRegistry projectileHitActionRegistry;
    @Mock
    private ProjectileLaunchSource projectileSource;
    @Mock
    private ProjectileRegistry projectileRegistry;
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

        ArrowLauncher launcher = new ArrowLauncher(audioEmitter, collisionResultMapper, gameEntityFinder, projectileHitActionRegistry, projectileRegistry, scheduler, properties, itemEffect);
        launcher.launch(launchContext);
        launcher.cancel();

        verify(soundPlaySchedule).stop();
    }

    @Test
    void launchLaunchesArrowProjectileAndPreparesTriggersWhichStartItemEffect() {
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        SimpleTriggerResult triggerResult = SimpleTriggerResult.ACTIVATES;

        Arrow arrow = mock(Arrow.class);
        when(arrow.getUniqueId()).thenReturn(ARROW_UNIQUE_ID);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        ProjectileLaunchSource projectileLaunchSource = mock(ProjectileLaunchSource.class);
        when(projectileLaunchSource.launchProjectile(eq(Arrow.class), any(Vector.class))).thenReturn(arrow);

        TriggerRun triggerRun = mock(TriggerRun.class);
        doAnswer(MockUtils.answerNotifyTriggerObserver(triggerResult)).when(triggerRun).addObserver(any(TriggerObserver.class));

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        ArrowProperties properties = new ArrowProperties(List.of(gameSound), VELOCITY);
        LaunchContext launchContext = new LaunchContext(damageSource, projectileLaunchSource, direction, () -> LAUNCH_DIRECTION, world);

        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);

        ArrowLauncher launcher = new ArrowLauncher(audioEmitter, collisionResultMapper, gameEntityFinder, projectileHitActionRegistry, projectileRegistry, scheduler, properties, itemEffect);
        launcher.addTriggerExecutor(triggerExecutor);
        launcher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getActor()).isInstanceOf(ProjectileActor.class);
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(damageSource);
            assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(direction);
        });

        verify(arrow).setPickupStatus(PickupStatus.DISALLOWED);
        verify(arrow).setVelocity(new Vector(0, 0, 3.0));
        verify(audioEmitter).playSound(gameSound, LAUNCH_DIRECTION);
        verify(projectileRegistry).register(ARROW_UNIQUE_ID);
        verify(projectileRegistry).unregister(ARROW_UNIQUE_ID);
    }
}
