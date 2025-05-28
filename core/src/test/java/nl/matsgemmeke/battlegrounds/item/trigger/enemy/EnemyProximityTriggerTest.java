package nl.matsgemmeke.battlegrounds.item.trigger.enemy;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EnemyProximityTriggerTest {

    private static final double RANGE = 2.5;

    private Entity entity;
    private Schedule schedule;
    private TargetFinder targetFinder;
    private TriggerObserver observer;
    private TriggerTarget target;

    @BeforeEach
    public void setUp() {
        entity = mock(Entity.class);
        schedule = mock(Schedule.class);
        targetFinder = mock(TargetFinder.class);
        observer = mock(TriggerObserver.class);
        target = mock(TriggerTarget.class);
    }

    @Test
    public void isStartedReturnsFalseWhenNotStarted() {
        EnemyProximityTrigger trigger = new EnemyProximityTrigger(schedule, targetFinder, RANGE);
        boolean started = trigger.isStarted();

        assertThat(started).isFalse();
    }

    @Test
    public void isStartedReturnsTrueWhenStarted() {
        Entity entity = mock(Entity.class);
        TriggerTarget target = mock(TriggerTarget.class);
        TriggerContext context = new TriggerContext(entity, target);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(schedule, targetFinder, RANGE);
        trigger.start(context);
        boolean started = trigger.isStarted();

        assertThat(started).isTrue();
    }

    @Test
    public void startStartsScheduleWithTaskThatStopsCheckingOnceTargetNoLongerExists() {
        TriggerContext context = new TriggerContext(entity, target);

        when(target.exists()).thenReturn(false);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(schedule, targetFinder, RANGE);
        trigger.addObserver(observer);
        trigger.start(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verifyNoInteractions(observer);
        verify(schedule).start();
        verify(schedule).stop();
    }

    @Test
    public void startStartsScheduleWithTaskThatDoesNothingWhenNoEnemyTargetsAreInsideRange() {
        Location targetLocation = new Location(null, 1, 1, 1);
        UUID entityId = UUID.randomUUID();

        when(entity.getUniqueId()).thenReturn(entityId);
        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(targetFinder.findEnemyTargets(entityId, targetLocation, RANGE)).thenReturn(Collections.emptyList());

        TriggerContext context = new TriggerContext(entity, target);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(schedule, targetFinder, RANGE);
        trigger.addObserver(observer);
        trigger.start(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verifyNoInteractions(observer);
        verify(schedule).start();
    }

    @Test
    public void startStartsScheduleWithTaskThatNotifiesObserversWhenEnemyTargetsAreInsideRange() {
        Location targetLocation = new Location(null, 1, 1, 1);
        UUID entityId = UUID.randomUUID();

        when(entity.getUniqueId()).thenReturn(entityId);
        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(targetFinder.findEnemyTargets(entityId, targetLocation, RANGE)).thenReturn(List.of(mock(GameEntity.class)));

        TriggerContext context = new TriggerContext(entity, target);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(schedule, targetFinder, RANGE);
        trigger.addObserver(observer);
        trigger.start(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer).onActivate();
        verify(schedule).start();
    }

    @Test
    public void stopStopsSchedule() {
        EnemyProximityTrigger trigger = new EnemyProximityTrigger(schedule, targetFinder, RANGE);
        trigger.stop();

        verify(schedule).stop();
    }
}
