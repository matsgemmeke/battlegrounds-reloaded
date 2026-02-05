package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static nl.matsgemmeke.battlegrounds.MockUtils.RUN_SCHEDULE_TASK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GunFireSimulationEffectPerformanceTest {

    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);
    private static final Location ACTOR_LOCATION = new Location(null, 1, 1, 1);
    private static final long BURST_INTERVAL = 1L;
    private static final long MAX_BURST_DURATION = 10L;
    private static final long MIN_BURST_DURATION = 5L;
    private static final long MAX_DELAY_DURATION = 10L;
    private static final long MIN_DELAY_DURATION = 5L;
    private static final long MAX_TOTAL_DURATION = 30L;
    private static final long MIN_TOTAL_DURATION = 20L;
    private static final List<GameSound> GENERIC_SHOTS_SOUNDS = Collections.emptyList();
    private static final GunFireSimulationProperties PROPERTIES = new GunFireSimulationProperties(GENERIC_SHOTS_SOUNDS, BURST_INTERVAL, MIN_BURST_DURATION, MAX_BURST_DURATION, MIN_DELAY_DURATION, MAX_DELAY_DURATION, MIN_TOTAL_DURATION, MAX_TOTAL_DURATION);

    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();
    private static final CollisionResult COLLISION_RESULT = new CollisionResult(null, null, null);

    @Mock(extraInterfaces = Removable.class)
    private Actor actor;
    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private DamageSource damageSource;
    @Mock
    private GunInfoProvider gunInfoProvider;
    @Mock
    private Scheduler scheduler;

    private GunFireSimulationEffectPerformance performance;

    @BeforeEach
    void setUp() {
        performance = new GunFireSimulationEffectPerformance(audioEmitter, gunInfoProvider, scheduler, PROPERTIES);
    }

    @Test
    void changeActorCreatesNewContextInstanceWithGivenActor() {
        Schedule schedule = mock(Schedule.class);
        Actor oldActor = mock(Actor.class);
        ItemEffectContext context = new ItemEffectContext(COLLISION_RESULT, damageSource, oldActor, INITIATION_LOCATION);
        Location newActorLocation = new Location(null, 1, 1, 1);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(newActorLocation);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(gunInfoProvider.getGunFireSimulationInfo(DAMAGE_SOURCE_ID)).thenReturn(Optional.empty());
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(schedule);

        performance.setContext(context);
        performance.start();
        performance.changeActor(actor);

        ArgumentCaptor<ScheduleTask> taskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(taskCaptor.capture());

        ScheduleTask task = taskCaptor.getValue();
        task.run();

        verify(audioEmitter).playSounds(GENERIC_SHOTS_SOUNDS, newActorLocation);
    }

    @Test
    void isPerformingReturnsFalseWhenNotPerforming() {
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void isPerformingReturnsTrueWhenPerforming() {
        ItemEffectContext context = this.createItemEffectContext();

        Schedule repeatingSchedule = mock(Schedule.class);
        when(repeatingSchedule.isRunning()).thenReturn(true);

        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);

        performance.perform(context);
        boolean performing = performance.isPerforming();

        assertThat(performing).isTrue();
    }

    @Test
    void performSimulatesGenericGunFireWhenGunInfoProviderHasNoInformationForEntity() {
        Schedule repeatingSchedule = mock(Schedule.class);
        ItemEffectContext context = this.createItemEffectContext();

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(ACTOR_LOCATION);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(gunInfoProvider.getGunFireSimulationInfo(DAMAGE_SOURCE_ID)).thenReturn(Optional.empty());
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);
        doAnswer(RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        performance.setContext(context);
        performance.start();

        verify(audioEmitter).playSounds(GENERIC_SHOTS_SOUNDS, ACTOR_LOCATION);
    }

    @Test
    void performStopsSimulatesGunFireOnceActorNoLongerExists() {
        Schedule repeatingSchedule = mock(Schedule.class);
        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 120;
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);
        ItemEffectContext context = this.createItemEffectContext();

        when(actor.exists()).thenReturn(false);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(gunInfoProvider.getGunFireSimulationInfo(DAMAGE_SOURCE_ID)).thenReturn(Optional.of(gunFireSimulationInfo));
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);
        doAnswer(RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        performance.setContext(context);
        performance.start();

        verifyNoInteractions(audioEmitter);
        verify(repeatingSchedule).stop();
    }

    @Test
    void performSimulatesGunFireOnceAndRemovesActorWhenFinished() {
        Schedule repeatingSchedule = mock(Schedule.class);
        ItemEffectContext context = this.createItemEffectContext();

        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 1200;
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(ACTOR_LOCATION);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(gunInfoProvider.getGunFireSimulationInfo(DAMAGE_SOURCE_ID)).thenReturn(Optional.of(gunFireSimulationInfo));
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);
        doAnswer(MockUtils.answerRunScheduleTask(MAX_TOTAL_DURATION)).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        performance.setContext(context);
        performance.start();

        // The implementation uses random variables, so check the max and min possible amount of executions
        verify(audioEmitter, atLeast(10)).playSounds(shotSounds, ACTOR_LOCATION);
        verify(audioEmitter, atMost(20)).playSounds(shotSounds, ACTOR_LOCATION);
        verify(repeatingSchedule, atLeast(1)).stop();
        verify(repeatingSchedule, atMost(10)).stop();
        verify((Removable) actor, atLeast(1)).remove();
        verify((Removable) actor, atMost(10)).remove();
    }

    private ItemEffectContext createItemEffectContext() {
        return new ItemEffectContext(COLLISION_RESULT, damageSource, actor, INITIATION_LOCATION);
    }
}
