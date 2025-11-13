package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
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
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HitscanLauncherTest {

    private static final long GAME_SOUND_DELAY = 5L;
    private static final ParticleEffect TRAJECTORY_PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 1, 0.0, 0.0, 0.0, 0.0, null, null);

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private CollisionDetector collisionDetector;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ParticleEffectSpawner particleEffectSpawner;
    @Mock
    private Scheduler scheduler;
    @Mock
    private TargetFinder targetFinder;

    @Test
    void cancelStopsOngoingSoundPlaySchedules() {
        Entity entity = mock(Entity.class);
        ProjectileLaunchSource source = mock(ProjectileLaunchSource.class);
        World world = mock(World.class);
        Location direction = new Location(world, 0.0, 0.0, 0.0, 0.0f, 0.0f);
        Schedule soundPlaySchedule = mock(Schedule.class);

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        HitscanProperties properties = new HitscanProperties(List.of(gameSound), TRAJECTORY_PARTICLE_EFFECT);
        LaunchContext launchContext = new LaunchContext(entity, source, direction, world);

        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);

        HitscanLauncher hitscanLauncher = new HitscanLauncher(audioEmitter, collisionDetector, particleEffectSpawner, scheduler, targetFinder, properties, itemEffect);
        hitscanLauncher.launch(launchContext);
        hitscanLauncher.cancel();

        verify(soundPlaySchedule).stop();
    }

    @Test
    void launchProducesProjectileStepUntilCollisionIsDetected() {
        ProjectileLaunchSource source = mock(ProjectileLaunchSource.class);
        World world = mock(World.class);
        Location direction = new Location(world, 0.0, 0.0, 0.0, 0.0f, 0.0f);
        Location hitLocation = new Location(world, 0.0, 0.0, 0.6, 0.0f, 0.0f);
        Material hitBlockMaterial = Material.STONE;

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(direction);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(hitBlockMaterial);
        when(hitBlock.getWorld()).thenReturn(world);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        HitscanProperties properties = new HitscanProperties(List.of(gameSound), TRAJECTORY_PARTICLE_EFFECT);
        LaunchContext launchContext = new LaunchContext(entity, source, direction, world);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(collisionDetector.producesBlockCollisionAt(hitLocation)).thenReturn(true);
        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);
        when(targetFinder.containsTargets(any(TargetQuery.class))).thenReturn(false);
        when(world.getBlockAt(hitLocation)).thenReturn(hitBlock);

        HitscanLauncher hitscanLauncher = new HitscanLauncher(audioEmitter, collisionDetector, particleEffectSpawner, scheduler, targetFinder, properties, itemEffect);
        hitscanLauncher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        ItemEffectContext itemEffectContext = itemEffectContextCaptor.getValue();
        assertThat(itemEffectContext.getEntity()).isEqualTo(entity);
        assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(hitLocation);
        assertThat(itemEffectContext.getSource().getLocation()).isEqualTo(hitLocation);

        verify(audioEmitter).playSound(gameSound, direction);
        verify(particleEffectSpawner, times(1)).spawnParticleEffect(eq(TRAJECTORY_PARTICLE_EFFECT), any(Location.class));
        verify(world).playEffect(hitLocation, org.bukkit.Effect.STEP_SOUND, hitBlockMaterial);
    }

    @Test
    void launchProducesProjectileStepUntilTargetsAreFound() {
        ProjectileLaunchSource source = mock(ProjectileLaunchSource.class);
        World world = mock(World.class);
        Location direction = new Location(world, 0.0, 0.0, 0.0, 0.0f, 0.0f);
        Location hitLocation = new Location(world, 0.0, 0.0, 0.6, 0.0f, 0.0f);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(direction);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getWorld()).thenReturn(world);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        HitscanProperties properties = new HitscanProperties(List.of(gameSound), TRAJECTORY_PARTICLE_EFFECT);
        LaunchContext launchContext = new LaunchContext(entity, source, direction, world);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);
        when(targetFinder.containsTargets(any(TargetQuery.class))).thenReturn(false, true);
        when(world.getBlockAt(hitLocation)).thenReturn(hitBlock);

        HitscanLauncher hitscanLauncher = new HitscanLauncher(audioEmitter, collisionDetector, particleEffectSpawner, scheduler, targetFinder, properties, itemEffect);
        hitscanLauncher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        ItemEffectContext itemEffectContext = itemEffectContextCaptor.getValue();
        assertThat(itemEffectContext.getEntity()).isEqualTo(entity);
        assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(hitLocation);
        assertThat(itemEffectContext.getSource().getLocation()).isEqualTo(hitLocation);

        verify(audioEmitter).playSound(gameSound, direction);
        verify(particleEffectSpawner, times(1)).spawnParticleEffect(eq(TRAJECTORY_PARTICLE_EFFECT), any(Location.class));
    }
}
