package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.MetadataValueCreator;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CombustionMechanismTest {

    private BukkitTask task;
    private MetadataValueCreator metadataValueCreator;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        metadataValueCreator = mock(MetadataValueCreator.class);
        task = mock(BukkitTask.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldCreateFireCircleAtHolderLocation() {
        int radius = 2;
        long delay = 0L;
        long ticksBetweenSpread = 5L;
        boolean burnBlocks = false;
        boolean spreadFire = true;

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

        when(taskRunner.runTaskTimer(any(Runnable.class), eq(delay), eq(ticksBetweenSpread))).thenReturn(task);

        CombustionMechanism mechanism = new CombustionMechanism(metadataValueCreator, taskRunner, radius, ticksBetweenSpread, burnBlocks, spreadFire);
        mechanism.activate(holder);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        // Simulate the task running three times to exceed the radius
        Runnable taskRunnable = runnableCaptor.getValue();
        taskRunnable.run();
        taskRunnable.run();
        taskRunnable.run();

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
        verify(taskRunner).runTaskTimer(taskRunnable, delay, ticksBetweenSpread);
    }

    @Test
    public void shouldCreateFireCircleAtDeployableObjectLocation() {
        int radius = 1;
        long delay = 0L;
        long ticksBetweenSpread = 5L;

        World world = mock(World.class);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mock(Block.class));

        ItemHolder holder = mock(ItemHolder.class);
        Location location = new Location(world, 0, 0, 0);

        Deployable object = mock(Deployable.class);
        when(object.getLocation()).thenReturn(location);
        when(object.getWorld()).thenReturn(world);

        when(taskRunner.runTaskTimer(any(Runnable.class), eq(delay), eq(ticksBetweenSpread))).thenReturn(task);

        CombustionMechanism mechanism = new CombustionMechanism(metadataValueCreator, taskRunner, radius, ticksBetweenSpread, false, false);
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
        verify(taskRunner).runTaskTimer(taskRunnable, delay, ticksBetweenSpread);
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
