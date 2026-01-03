package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FireballLauncherTest {

    private static final double VELOCITY = 2.0;
    private static final long GAME_SOUND_DELAY = 5L;
    private static final ParticleEffect TRAJECTORY_PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 1, 0.0, 0.0, 0.0, 0.0, null, null);
    private static final Location LAUNCH_DIRECTION = new Location(null, 1, 1, 1);

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private DamageSource damageSource;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ParticleEffectSpawner particleEffectSpawner;
    @Mock
    private ProjectileHitActionRegistry projectileHitActionRegistry;
    @Mock
    private ProjectileLaunchSource projectileSource;
    @Mock
    private Scheduler scheduler;

    @Test
    void cancelStopsOngoingSoundPlaySchedules() {
        SmallFireball fireball = mock(SmallFireball.class);
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);
        Schedule soundPlaySchedule = mock(Schedule.class);

        Schedule repeatingSchedule = mock(Schedule.class);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        FireballProperties properties = new FireballProperties(List.of(gameSound), TRAJECTORY_PARTICLE_EFFECT, VELOCITY);
        LaunchContext context = new LaunchContext(damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        when(projectileSource.launchProjectile(eq(SmallFireball.class), any(Vector.class))).thenReturn(fireball);
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);

        FireballLauncher launcher = new FireballLauncher(audioEmitter, particleEffectSpawner, projectileHitActionRegistry, scheduler, properties, itemEffect);
        launcher.launch(context);
        launcher.cancel();

        verify(soundPlaySchedule).stop();
    }

    @Test
    void launchStartsScheduleThatActivatesEffectWhenProjectileHitActionGetsCalled() {
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);
        Location fireballLocation = new Location(world, 1.0, 1.0, 1.0);

        SmallFireball fireball = mock(SmallFireball.class);
        when(fireball.getLocation()).thenReturn(fireballLocation);
        when(fireball.getWorld()).thenReturn(world);

        ProjectileLaunchSource source = mock(ProjectileLaunchSource.class);
        when(source.launchProjectile(eq(SmallFireball.class), any(Vector.class))).thenReturn(fireball);

        Schedule repeatingSchedule = mock(Schedule.class);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        FireballProperties properties = new FireballProperties(List.of(gameSound), TRAJECTORY_PARTICLE_EFFECT, VELOCITY);
        LaunchContext context = new LaunchContext(damageSource, source, direction, () -> LAUNCH_DIRECTION, world);

        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);
        doAnswer(MockUtils.answerRunProjectileHitAction(null)).when(projectileHitActionRegistry).registerProjectileHitAction(eq(fireball), any(ProjectileHitAction.class));

        FireballLauncher launcher = new FireballLauncher(audioEmitter, particleEffectSpawner, projectileHitActionRegistry, scheduler, properties, itemEffect);
        launcher.launch(context);
        
        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(damageSource);
            assertThat(itemEffectContext.getEffectSource()).satisfies(effectSource -> {
                assertThat(effectSource.getLocation()).isEqualTo(fireballLocation);
                assertThat(effectSource.getWorld()).isEqualTo(world);
            });
            assertThat(itemEffectContext.getTriggerTarget()).satisfies(triggerTarget -> {
                assertThat(triggerTarget.getLocation()).isEqualTo(fireballLocation);
                assertThat(triggerTarget.getWorld()).isEqualTo(world);
            });
            assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(direction);
        });

        verify(audioEmitter).playSound(gameSound, LAUNCH_DIRECTION);
        verify(particleEffectSpawner).spawnParticleEffect(TRAJECTORY_PARTICLE_EFFECT, fireballLocation);
        verify(repeatingSchedule).stop();
    }
}
