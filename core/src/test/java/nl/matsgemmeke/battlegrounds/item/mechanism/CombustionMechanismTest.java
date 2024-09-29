package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.MetadataValueCreator;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CombustionMechanismTest {

    private static final double LONG_RANGE_DAMAGE = 25.0;
    private static final double LONG_RANGE_DISTANCE = 10.0;
    private static final double MEDIUM_RANGE_DAMAGE = 75.0;
    private static final double MEDIUM_RANGE_DISTANCE = 5.0;
    private static final double SHORT_RANGE_DAMAGE = 150.0;
    private static final double SHORT_RANGE_DISTANCE = 2.5;

    private AudioEmitter audioEmitter;
    private BukkitTask task;
    private CollisionDetector collisionDetector;
    private MetadataValueCreator metadataValueCreator;
    private RangeProfile rangeProfile;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        task = mock(BukkitTask.class);
        collisionDetector = mock(CollisionDetector.class);
        metadataValueCreator = mock(MetadataValueCreator.class);
        rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldCreateFireCircleAtHolderLocation() {
        int radius = 2;
        long ticksBetweenFireSpread = 5L;
        boolean burnBlocks = false;
        boolean spreadFire = true;

        CombustionSettings settings = new CombustionSettings(Collections.emptyList(), radius, ticksBetweenFireSpread, burnBlocks, spreadFire);

        MetadataValue metadataBurnBlocks = mock(MetadataValue.class);
        MetadataValue metadataSpreadFire = mock(MetadataValue.class);

        when(metadataValueCreator.createFixedMetadataValue(burnBlocks)).thenReturn(metadataBurnBlocks);
        when(metadataValueCreator.createFixedMetadataValue(spreadFire)).thenReturn(metadataSpreadFire);

        World world = mock(World.class);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mock(Block.class));

        Location location = new Location(world, 0, 0, 0);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getLocation()).thenReturn(location);
        when(holder.getWorld()).thenReturn(world);

        Block middleBlock = this.createBlock(world, 0, 0, 0, Material.AIR, true);
        Block leftBlock = this.createBlock(world, -1, 0, 0, Material.AIR, true);
        Block rightBlock = this.createBlock(world, 1, 0, 0, Material.AIR, true);
        Block upperBlock = this.createBlock(world, 0, 0, -1, Material.AIR, true);
        Block lowerBlock = this.createBlock(world, 0, 0, 1, Material.STONE, false);
        Block blockOutsideLineOfSight = this.createBlock(world, 0, 0, 2, Material.AIR, true);

        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(ticksBetweenFireSpread))).thenReturn(task);

        CombustionMechanism mechanism = new CombustionMechanism(settings, audioEmitter, collisionDetector, rangeProfile, metadataValueCreator, taskRunner);
        mechanism.activate(holder);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        // Simulate the task running three times to exceed the radius
        Runnable runnable = runnableCaptor.getValue();
        runnable.run();
        runnable.run();
        runnable.run();

        verify(middleBlock).setType(Material.FIRE);
        verify(middleBlock).setMetadata("battlegrounds-burn-blocks", metadataBurnBlocks);
        verify(middleBlock).setMetadata("battlegrounds-spread-fire", metadataSpreadFire);

        verify(leftBlock).setType(Material.FIRE);
        verify(leftBlock).setMetadata("battlegrounds-burn-blocks", metadataBurnBlocks);
        verify(leftBlock).setMetadata("battlegrounds-spread-fire", metadataSpreadFire);

        verify(rightBlock).setType(Material.FIRE);
        verify(rightBlock).setMetadata("battlegrounds-burn-blocks", metadataBurnBlocks);
        verify(rightBlock).setMetadata("battlegrounds-spread-fire", metadataSpreadFire);

        verify(upperBlock).setType(Material.FIRE);
        verify(upperBlock).setMetadata("battlegrounds-burn-blocks", metadataBurnBlocks);
        verify(upperBlock).setMetadata("battlegrounds-spread-fire", metadataSpreadFire);

        verify(lowerBlock, never()).setType(Material.FIRE);
        verify(lowerBlock, never()).setMetadata(anyString(), any());

        verify(blockOutsideLineOfSight, never()).setType(Material.FIRE);
        verify(blockOutsideLineOfSight, never()).setMetadata(anyString(), any());

        verify(task).cancel();
    }

    @Test
    public void shouldCreateFireCircleAtDeployableObjectLocationAndRemoveObject() {
        int radius = 1;
        long ticksBetweenSpread = 5L;

        CombustionSettings settings = new CombustionSettings(Collections.emptyList(), radius, ticksBetweenSpread, false, false);

        World world = mock(World.class);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mock(Block.class));

        ItemHolder holder = mock(ItemHolder.class);
        Location location = new Location(world, 0, 0, 0);

        Deployable object = mock(Deployable.class);
        when(object.getLocation()).thenReturn(location);
        when(object.getWorld()).thenReturn(world);

        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(ticksBetweenSpread))).thenReturn(task);

        CombustionMechanism mechanism = new CombustionMechanism(settings, audioEmitter, collisionDetector, rangeProfile, metadataValueCreator, taskRunner);
        mechanism.activate(holder, object);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        // Simulate the task running twice to exceed the radius
        Runnable taskRunnable = runnableCaptor.getValue();
        taskRunnable.run();
        taskRunnable.run();
        taskRunnable.run();

        verify(object).remove();

        verify(task).cancel();
    }

    @Test
    public void damageNearbyEntitiesInsideTheLongRangeDistance() {
        CombustionSettings settings = new CombustionSettings(Collections.emptyList(), 0, 0, false, false);

        World world = mock(World.class);
        Location holderLocation = new Location(world, 4, 0, 0);
        Location objectLocation = new Location(world, 0, 0, 0);
        Location targetLocation = new Location(world, 2, 0, 0);

        GameEntity target = mock(GameEntity.class);
        when(target.getLocation()).thenReturn(targetLocation);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getLocation()).thenReturn(holderLocation);

        Deployable object = mock(Deployable.class);
        when(object.getLocation()).thenReturn(objectLocation);
        when(object.getWorld()).thenReturn(world);

        when(collisionDetector.findTargets(holder, objectLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(holder, target));

        CombustionMechanism mechanism = new CombustionMechanism(settings, audioEmitter, collisionDetector, rangeProfile, metadataValueCreator, taskRunner);
        mechanism.activate(holder, object);

        verify(holder).damage(MEDIUM_RANGE_DAMAGE);
        verify(target).damage(SHORT_RANGE_DAMAGE);
    }

    @Test
    public void playCombustionSoundAtActivationLocation() {
        GameSound sound = mock(GameSound.class);
        List<GameSound> sounds = List.of(sound);

        CombustionSettings settings = new CombustionSettings(sounds, 0, 0, false, false);

        World world = mock(World.class);
        Location location = new Location(world, 0, 0, 0);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getLocation()).thenReturn(location);
        when(holder.getWorld()).thenReturn(world);

        CombustionMechanism mechanism = new CombustionMechanism(settings, audioEmitter, collisionDetector, rangeProfile, metadataValueCreator, taskRunner);
        mechanism.activate(holder);

        verify(audioEmitter).playSounds(sounds, location);
    }

    @NotNull
    private Block createBlock(World world, int x, int y, int z, Material material, boolean passable) {
        Block block = mock(Block.class);
        when(block.getLocation()).thenReturn(new Location(world, x, y, z));
        when(block.getType()).thenReturn(material).thenReturn(Material.FIRE);
        when(block.isPassable()).thenReturn(passable);
        when(world.getBlockAt(x, y, z)).thenReturn(block);

        return block;
    }
}
