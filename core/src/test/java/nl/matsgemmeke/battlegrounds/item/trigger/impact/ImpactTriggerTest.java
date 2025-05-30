package nl.matsgemmeke.battlegrounds.item.trigger.impact;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ImpactTriggerTest {

    private Entity entity;
    private Schedule schedule;
    private TriggerObserver observer;
    private TriggerTarget target;

    @BeforeEach
    public void setUp() {
        entity = mock(Entity.class);
        schedule = mock(Schedule.class);
        observer = mock(TriggerObserver.class);
        target = mock(TriggerTarget.class);
    }

    @Test
    public void isStartedReturnsFalseWhenNotStarted() {
        ImpactTrigger trigger = new ImpactTrigger(schedule);
        boolean started = trigger.isStarted();

        assertThat(started).isFalse();
    }

    @Test
    public void isStartedReturnsTrueWhenStarted() {
        TriggerContext context = new TriggerContext(entity, target);

        ImpactTrigger trigger = new ImpactTrigger(schedule);
        trigger.start(context);
        boolean started = trigger.isStarted();

        assertThat(started).isTrue();
    }

    @Test
    public void startStartsScheduleWithTaskThatStopsCheckingOnceTargetNoLongerExists() {
        TriggerContext context = new TriggerContext(entity, target);

        when(target.exists()).thenReturn(false);

        ImpactTrigger trigger = new ImpactTrigger(schedule);
        trigger.start(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(schedule).start();
        verify(schedule).stop();
    }

    @Test
    public void startStartsScheduleWithTaskThatDoesNotNotifyObservers() {
        TriggerContext context = new TriggerContext(entity, target);
        Vector velocity = new Vector();

        when(target.exists()).thenReturn(true);
        when(target.getVelocity()).thenReturn(velocity);

        ImpactTrigger trigger = new ImpactTrigger(schedule);
        trigger.addObserver(observer);
        trigger.start(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer, never()).onActivate();
        verify(schedule).start();
    }

    @Test
    public void startStartsScheduleWithTaskThatDoesNotNotifyObserversWhenCastRayTraceResultIsNull() {
        TriggerContext context = new TriggerContext(entity, target);
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 1.0)).thenReturn(null);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        ImpactTrigger trigger = new ImpactTrigger(schedule);
        trigger.addObserver(observer);
        trigger.start(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer, never()).onActivate();
        verify(schedule).start();
    }

    @Test
    public void startStartsScheduleWithTaskThatDoesNotNotifyObserversWhenCastRayTraceResultHasNoHitBlock() {
        TriggerContext context = new TriggerContext(entity, target);
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), (Block) null, null);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        ImpactTrigger trigger = new ImpactTrigger(schedule);
        trigger.addObserver(observer);
        trigger.start(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer, never()).onActivate();
        verify(schedule).start();
    }

    @Test
    public void startStartsScheduleWithTaskThatDoesNotNotifyObserversWhenCastRayTraceResultHasNoHitBlockFace() {
        TriggerContext context = new TriggerContext(entity, target);
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        Block hitBlock = mock(Block.class);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, null);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        ImpactTrigger trigger = new ImpactTrigger(schedule);
        trigger.addObserver(observer);
        trigger.start(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer, never()).onActivate();
        verify(schedule).start();
    }

    @Test
    public void startStartsScheduleWithTaskThatDoesNotNotifyObserversWhenCastRayTraceResultHitsBlockThatIsNotSolid() {
        TriggerContext context = new TriggerContext(entity, target);
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(Material.AIR);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, BlockFace.NORTH);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        ImpactTrigger trigger = new ImpactTrigger(schedule);
        trigger.addObserver(observer);
        trigger.start(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer, never()).onActivate();
        verify(schedule).start();
    }

    @Test
    public void startStartsScheduleWithTaskThatNotifiesObserversWhenCastRayTraceResultHitsSolidBlock() {
        TriggerContext context = new TriggerContext(entity, target);
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(Material.STONE);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, BlockFace.NORTH);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        ImpactTrigger trigger = new ImpactTrigger(schedule);
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
        ImpactTrigger trigger = new ImpactTrigger(schedule);
        trigger.stop();

        verify(schedule).stop();
    }
}
