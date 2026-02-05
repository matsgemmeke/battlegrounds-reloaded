package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.MetadataValueEditor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static nl.matsgemmeke.battlegrounds.ArgumentMatcherUtils.isBetween;
import static nl.matsgemmeke.battlegrounds.MockUtils.RUN_SCHEDULE_TASK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CombustionEffectPerformanceTest {

    private static final boolean BURN_BLOCKS = false;
    private static final boolean SPREAD_FIRE = true;
    private static final double GROWTH = 0.5;
    private static final double MIN_SIZE = 0.5;
    private static final double MAX_SIZE = 1.5;
    private static final double LONG_RANGE_DAMAGE = 25.0;
    private static final double LONG_RANGE_DISTANCE = 10.0;
    private static final double MEDIUM_RANGE_DAMAGE = 75.0;
    private static final double MEDIUM_RANGE_DISTANCE = 5.0;
    private static final double SHORT_RANGE_DAMAGE = 150.0;
    private static final double SHORT_RANGE_DISTANCE = 2.5;
    private static final List<GameSound> COMBUSTION_SOUNDS = Collections.emptyList();
    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);
    private static final long GROWTH_INTERVAL = 5L;
    private static final long MIN_DURATION = 500L;
    private static final long MAX_DURATION = 600L;
    private static final RangeProfile RANGE_PROFILE = new RangeProfile(SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE);
    private static final CombustionProperties PROPERTIES = new CombustionProperties(COMBUSTION_SOUNDS, RANGE_PROFILE, MIN_SIZE, MAX_SIZE, GROWTH, GROWTH_INTERVAL, MIN_DURATION, MAX_DURATION, BURN_BLOCKS, SPREAD_FIRE);

    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();
    private static final CollisionResult COLLISION_RESULT = new CollisionResult(null, null, null);

    @Mock(extraInterfaces = Removable.class)
    private Actor actor;
    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private CollisionDetector collisionDetector;
    @Mock
    private DamageSource damageSource;
    @Mock
    private MetadataValueEditor metadataValueEditor;
    @Mock
    private Scheduler scheduler;
    @Mock
    private TargetFinder targetFinder;

    private CombustionEffectPerformance performance;

    @BeforeEach
    void setUp() {
        performance = new CombustionEffectPerformance(audioEmitter, collisionDetector, metadataValueEditor, scheduler, targetFinder, PROPERTIES);
    }

    @Test
    void isPerformingReturnsFalseWhenNotPerforming() {
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void isPerformingReturnsTrueWhenPerforming() {
        ItemEffectContext context = this.createItemEffectContext();
        Schedule cancelSchedule = mock(Schedule.class);

        Schedule repeatingSchedule = mock(Schedule.class);
        when(repeatingSchedule.isRunning()).thenReturn(true);

        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(isBetween(MIN_DURATION, MAX_DURATION)))).thenReturn(cancelSchedule);

        performance.perform(context);
        boolean performing = performance.isPerforming();

        assertThat(performing).isTrue();
    }

    @Test
    void performCreatesFireCircleAtSourceLocationAndResetsEffectAfterMaxDuration() {
        ItemEffectContext context = this.createItemEffectContext();
        Schedule cancelSchedule = mock(Schedule.class);
        Schedule repeatingSchedule = mock(Schedule.class);

        World world = mock(World.class);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mock(Block.class));

        Location actorLocation = new Location(world, 0, 0, 0);
        Location targetLocation = new Location(world, 6, 0, 0);

        GameEntity target = mock(GameEntity.class);
        when(target.getLocation()).thenReturn(targetLocation);

        Block middleBlock = mock(Block.class);
        when(middleBlock.getLocation()).thenReturn(new Location(world, 0, 0, 0));
        when(middleBlock.getType()).thenReturn(Material.AIR).thenReturn(Material.FIRE);
        when(world.getBlockAt(0, 0, 0)).thenReturn(middleBlock);

        Block leftBlock = mock(Block.class);
        when(leftBlock.getLocation()).thenReturn(new Location(world, -1, 0, 0));
        when(leftBlock.getType()).thenReturn(Material.AIR).thenReturn(Material.FIRE);
        when(world.getBlockAt(-1, 0, 0)).thenReturn(leftBlock);

        Block rightBlock = mock(Block.class);
        when(rightBlock.getLocation()).thenReturn(new Location(world, 1, 0, 0));
        when(rightBlock.getType()).thenReturn(Material.AIR).thenReturn(Material.FIRE);
        when(world.getBlockAt(1, 0, 0)).thenReturn(rightBlock);

        Block upperBlock = mock(Block.class);
        when(upperBlock.getLocation()).thenReturn(new Location(world, 0, 0, -1));
        when(upperBlock.getType()).thenReturn(Material.AIR).thenReturn(Material.FIRE);
        when(world.getBlockAt(0, 0, -1)).thenReturn(upperBlock);

        Block lowerBlock = mock(Block.class);
        when(lowerBlock.getType()).thenReturn(Material.STONE).thenReturn(Material.FIRE);
        when(world.getBlockAt(0, 0, 1)).thenReturn(lowerBlock);

        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getWorld()).thenReturn(world);
        when(collisionDetector.hasLineOfSight(any(Location.class), any(Location.class))).thenReturn(true);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(duration -> duration >= MIN_DURATION && duration <= MAX_DURATION))).thenReturn(cancelSchedule);
        when(targetFinder.findTargets(DAMAGE_SOURCE_ID, actorLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(target));

        performance.perform(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(repeatingSchedule).addTask(scheduleTaskCaptor.capture());

        // Simulate the task running three times to exceed the max size
        ScheduleTask task = scheduleTaskCaptor.getValue();
        task.run();
        task.run();
        task.run();

        ArgumentCaptor<ScheduleTask> cancelTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(cancelSchedule).addTask(cancelTaskCaptor.capture());

        cancelTaskCaptor.getValue().run();

        verify(middleBlock).setType(Material.FIRE);
        verify(middleBlock).setType(Material.AIR);
        verify(metadataValueEditor).addFixedMetadataValue(middleBlock, "burn-blocks", BURN_BLOCKS);
        verify(metadataValueEditor).addFixedMetadataValue(middleBlock, "spread-fire", SPREAD_FIRE);
        verify(metadataValueEditor).removeMetadata(middleBlock, "burn-blocks");
        verify(metadataValueEditor).removeMetadata(middleBlock, "spread-fire");

        verify(leftBlock).setType(Material.FIRE);
        verify(leftBlock).setType(Material.AIR);
        verify(metadataValueEditor).addFixedMetadataValue(leftBlock, "burn-blocks", BURN_BLOCKS);
        verify(metadataValueEditor).addFixedMetadataValue(leftBlock, "spread-fire", SPREAD_FIRE);
        verify(metadataValueEditor).removeMetadata(leftBlock, "burn-blocks");
        verify(metadataValueEditor).removeMetadata(leftBlock, "spread-fire");

        verify(rightBlock).setType(Material.FIRE);
        verify(rightBlock).setType(Material.AIR);
        verify(metadataValueEditor).addFixedMetadataValue(rightBlock, "burn-blocks", BURN_BLOCKS);
        verify(metadataValueEditor).addFixedMetadataValue(rightBlock, "spread-fire", SPREAD_FIRE);
        verify(metadataValueEditor).removeMetadata(rightBlock, "burn-blocks");
        verify(metadataValueEditor).removeMetadata(rightBlock, "spread-fire");

        verify(upperBlock).setType(Material.FIRE);
        verify(upperBlock).setType(Material.AIR);
        verify(metadataValueEditor).addFixedMetadataValue(upperBlock, "burn-blocks", BURN_BLOCKS);
        verify(metadataValueEditor).addFixedMetadataValue(upperBlock, "spread-fire", SPREAD_FIRE);
        verify(metadataValueEditor).removeMetadata(upperBlock, "burn-blocks");
        verify(metadataValueEditor).removeMetadata(upperBlock, "spread-fire");

        verify(lowerBlock, never()).setType(Material.FIRE);
        verify(lowerBlock, never()).setType(Material.AIR);
        verify(metadataValueEditor, never()).addFixedMetadataValue(eq(lowerBlock), anyString(), any());
        verify(metadataValueEditor, never()).removeMetadata(eq(lowerBlock), anyString());

        verify(audioEmitter).playSounds(COMBUSTION_SOUNDS, actorLocation);
        verify(repeatingSchedule, times(2)).stop();
        verify(target).damage(new Damage(LONG_RANGE_DAMAGE, DamageType.FIRE_DAMAGE));
        verify((Removable) actor).remove();
    }

    @Test
    void rollbackResetsAffectedBlocksAndTriggerRuns() {
        ItemEffectContext context = this.createItemEffectContext();
        Location blockLocation = new Location(null, 0, 0, 0);
        Location actorLocation = new Location(null, 0, 0, 0);
        Schedule cancelSchedule = mock(Schedule.class);

        Block block = mock(Block.class);
        when(block.getLocation()).thenReturn(blockLocation);
        when(block.getType()).thenReturn(Material.AIR, Material.FIRE);

        World world = mock(World.class);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(block);

        Schedule repeatingSchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getWorld()).thenReturn(world);
        when(collisionDetector.hasLineOfSight(blockLocation, actorLocation)).thenReturn(true);
        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(duration -> duration >= MIN_DURATION && duration <= MAX_DURATION))).thenReturn(cancelSchedule);

        performance.perform(context);
        performance.rollback();

        verify(block).setType(Material.AIR);
        verify(metadataValueEditor).removeMetadata(block, "burn-blocks");
        verify(metadataValueEditor).removeMetadata(block, "spread-fire");
    }

    private ItemEffectContext createItemEffectContext() {
        return new ItemEffectContext(COLLISION_RESULT, damageSource, actor, INITIATION_LOCATION);
    }
}
