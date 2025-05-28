package nl.matsgemmeke.battlegrounds.item.trigger.floor;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FloorHitTriggerTest {

    private Schedule schedule;
    private TriggerContext context;
    private TriggerObserver observer;
    private TriggerTarget target;

    @BeforeEach
    public void setUp() {
        schedule = mock(Schedule.class);
        observer = mock(TriggerObserver.class);
        target = mock(TriggerTarget.class);
        context = new TriggerContext(mock(Entity.class), target);
    }

    @Test
    public void isStartedReturnsFalseWhenTriggerIsNotStarted() {
        FloorHitTrigger trigger = new FloorHitTrigger(schedule);
        boolean started = trigger.isStarted();

        assertThat(started).isFalse();
    }

    @Test
    public void isStartedReturnsTrueWhenTriggerIsStarted() {
        FloorHitTrigger trigger = new FloorHitTrigger(schedule);
        trigger.start(context);
        boolean started = trigger.isStarted();

        assertThat(started).isTrue();
    }

    @Test
    public void startStartsScheduleWithTaskThatStopsCheckingOnceTargetNoLongerExists() {
        when(target.exists()).thenReturn(false);

        FloorHitTrigger trigger = new FloorHitTrigger(schedule);
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
    public void startStartsScheduleWithTaskThatNotifiesObserversOnceBlockBelowObjectIsNotPassable() {
        World world = mock(World.class);
        Location targetLocation = new Location(world, 1, 1, 1);

        Block blockBelowObject = mock(Block.class);
        when(blockBelowObject.isPassable()).thenReturn(true).thenReturn(false);
        when(world.getBlockAt(any(Location.class))).thenReturn(blockBelowObject);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);

        FloorHitTrigger trigger = new FloorHitTrigger(schedule);
        trigger.addObserver(observer);
        trigger.start(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        ScheduleTask task = scheduleTaskCaptor.getValue();
        task.run();
        task.run();

        verify(observer).onActivate();
        verify(schedule).start();
    }

    @Test
    public void stopStopsSchedule() {
        FloorHitTrigger trigger = new FloorHitTrigger(schedule);
        trigger.stop();

        verify(schedule).stop();
    }
}
